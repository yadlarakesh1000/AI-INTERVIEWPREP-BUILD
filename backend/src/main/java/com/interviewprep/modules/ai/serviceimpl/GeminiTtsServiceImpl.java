package com.interviewprep.modules.ai.serviceimpl;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.config.AiConfig;
import com.interviewprep.modules.ai.service.TextToSpeechService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiTtsServiceImpl implements TextToSpeechService  {
  private static final String GEMINI_BASE_URL="https://generativelanguage.googleapis.com/v1beta/models/";

private static final String TTS_MODEL="gemini-2.5-flash-preview-tts";
private static final String VOICE_NAME = "Kore";
private static final int DEFAULT_SAMPLE_RATE =24000;
private static final Pattern RATE_PATTERN = Pattern.compile("rate=(\\d+)");
  
private  final AiConfig aiConfig;
private  final RestTemplate geminiRestTemplate;
private final ObjectMapper objectMapper;

  @Override
  public byte[] synthesize(String text) {

            if(text==null || text.isBlank()){
               return null;
            }
           if (aiConfig.getGeminiApiKey() == null || aiConfig.getGeminiApiKey().isBlank()) {
           
            log.warn("Gemini API key not configured; skipping TTS (frontend will use Web Speech API)");
            return null;
        }
        String url = GEMINI_BASE_URL+TTS_MODEL+":generateContent?key="+aiConfig.getGeminiApiKey();

         HttpEntity<Map<String, Object>> request = buildRequest(text);
          log.debug("Gemini TTS request -> model={}, {} chars", TTS_MODEL, text.length());
          int attempt = 0;
          while(true){
            try{
              ResponseEntity<String> response = geminiRestTemplate.postForEntity(url, request, String.class);
              return parseAudio(response.getBody());
            }
            catch (HttpServerErrorException | ResourceAccessException ex) {
                if (attempt > 2) {
                    log.error("Gemini TTS failed after retry; returning null for browser fallback", ex);
                    return null;
                }
          }
          catch(HttpClientErrorException ex){
              log.error("Unexpected Gemini TTS error; returning null for browser fallback", ex);
              return null;
          }
  
  }
}
// extract audio from the text 
  private byte[] parseAudio(String body) {
           try{
            JsonNode inlineData = objectMapper.readTree(body)
            .path("candidates").path(0)
            .path("content").path("parts").path(0)
            .path("inlineData");
            String base64Audio = inlineData.path("data").asText(null);
            if(base64Audio == null || base64Audio.isBlank()){
              log.error("Gemini TTS response contained no audio data; returning null for browser fallback");
              return null;
           }
           String mimeType = inlineData.path("mimeTpe").asText("");
           int sampleRate = parseSampleRate(mimeType);
           byte[] pcm = Base64.getDecoder().decode(base64Audio);
           
            log.debug("Gemini TTS produced {} PCM bytes at {}Hz", pcm.length, sampleRate);
            return pcmToWav(pcm,sampleRate);
               }
               catch(Exception ex){
               log.error("Failed to parse Gemini TTS response; returning null for browser fallback", ex);
               return null;
               }
              }

  private byte[] pcmToWav(byte[] pcm, int sampleRate) {
       int channels =1;
       int bitsPerSample = 16;
       int byteRate = sampleRate*channels*bitsPerSample /8;
       int blockAlign = channels * bitsPerSample /8;
       int dataLen = pcm.length;
       ByteBuffer buffer = ByteBuffer.allocate(44+dataLen).order(ByteOrder.LITTLE_ENDIAN);
       buffer.put("RIFF".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(36 + dataLen);
        buffer.put("WAVE".getBytes(StandardCharsets.US_ASCII));
        buffer.put("fmt ".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(16);                     // PCM fmt chunk size
        buffer.putShort((short) 1);            // audio format = PCM
        buffer.putShort((short) channels);
        buffer.putInt(sampleRate);
        buffer.putInt(byteRate);
        buffer.putShort((short) blockAlign);
        buffer.putShort((short) bitsPerSample);
        buffer.put("data".getBytes(StandardCharsets.US_ASCII));
        buffer.putInt(dataLen);
        buffer.put(pcm);
        return buffer.array();

  }
  private int parseSampleRate(String mimeType) {
       if(mimeType != null ){
        Matcher matcher = RATE_PATTERN.matcher(mimeType);
        if(matcher.find()){
            return Integer.parseInt(matcher.group(1));
        }
       }
       return DEFAULT_SAMPLE_RATE;
  }
  private HttpEntity<Map<String, Object>> buildRequest(String text) {
            Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", text)))),
                "generationConfig", Map.of(
                        "responseModalities", List.of("AUDIO"),
                        "speechConfig", Map.of(
                                "voiceConfig", Map.of(
                                        "prebuiltVoiceConfig", Map.of("voiceName", VOICE_NAME)))));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(body,httpHeaders);
  }
  
}
