package com.interviewprep.modules.ai.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.interviewprep.exception.BadRequestException;
import com.interviewprep.modules.ai.config.GroqConfig;
import com.interviewprep.modules.ai.dto.ResumeData;
import com.interviewprep.modules.ai.dto.ResumeData.ExperienceInfo;
import com.interviewprep.modules.ai.dto.ResumeData.ProjectInfo;
import com.interviewprep.modules.ai.service.ResumeParsingService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfBoxGroqResumeParsingServiceImpl implements ResumeParsingService {
    private static final double PARSE_TEMPERATURE=0.1;
  private static final  String STRICTER_SUFFIX	="\n\nIMPORTANT: Respond with ONLY minified, valid JSON matching the exact keys above. No markdown, no backticks, no commentary.";

          private final GroqConfig groqConfig;
          private final ObjectMapper objectMapper;
          private final RestTemplate groqRestTemplate;
  @Override
  public ResumeData parseResumeText(String extractedText) {
    
      if(extractedText ==null || extractedText.isBlank()){
        log.warn("Resume text is empty; returning empty resume data");
        return emptyResumeData();
      }
      String prompt =buildPrompt(extractedText);
      try{
       return parseResumeJson(callGroqChat(prompt));
        
      }
      catch(Exception ex){
        log.error("Parsing resume failed on first attempt,retrying with stricter prompt",ex);
      }
      try{
          String strictPrompt = prompt+STRICTER_SUFFIX;
         return parseResumeJson(callGroqChat(strictPrompt));
          
      } 
      catch(Exception ex){
        log.error("parsing failed after retry returning empty resume data",ex);
        return emptyResumeData();
      }
  }

   private String buildPrompt(String extractedText){ 
               return """
                You are a resume parser. Extract structured data from the following resume text.

                Resume Text:
                %s

                Respond in this exact JSON format (no markdown, no backticks):
                {
                    "skills": ["Java", "Spring Boot", ...],
                    "projects": [
                        {
                            "name": "Project Name",
                            "description": "Brief description",
                            "technologies": ["Tech1", "Tech2"]
                        }
                    ],
                    "experience": [
                        {
                            "role": "Job Title",
                            "company": "Company Name",
                            "duration": "Start - End",
                            "description": "Brief description"
                        }
                    ],
                    "education": "Degree, University, Year"
                }""".formatted(extractedText);
   }

   private ResumeData parseResumeJson(String content) throws Exception{
    JsonNode root = objectMapper.readTree(extractJsonObject(content));
    List<String> skills = readStringList(root.path("skills"));
    List<ProjectInfo>projects  = readProjects(root.path("projects"));
    List<ExperienceInfo> experience = readExperience(root.path("experience"));
    String education = readText(root.path("education"));
    ResumeData data  = ResumeData.builder()
                           .skills(skills)
                           .projects(projects)
                           .experience(experience).
                           education(education).
                           build(); 
       log.debug("Parsed resume: {} skills, {} projects, {} experience entries",
                skills.size(), projects.size(), experience.size());
                return data;
   }
   private List<String> readStringList(JsonNode arrayNode){
    List<String> result = new ArrayList<>();
      if(arrayNode !=null && arrayNode.isArray()){
        
        arrayNode.forEach(item -> {
            String value =item.asText(null);
            if(value != null && !value.isBlank()){
              result.add(value);
            }
        });
      }
    return result;
   }
    private List<ProjectInfo> readProjects(JsonNode arrayNode) {
        List<ProjectInfo> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(node -> result.add(ProjectInfo.builder()
                    .name(readText(node.path("name")))
                    .description(readText(node.path("description")))
                    .technologies(readStringList(node.path("technologies")))
                    .build()));
        }
        return result;
    }
    private List<ExperienceInfo> readExperience(JsonNode arrayNode){
        List<ExperienceInfo> result = new ArrayList<>();
        if (arrayNode != null && arrayNode.isArray()) {
            arrayNode.forEach(node -> result.add(ExperienceInfo.builder()
                    .role(readText(node.path("role")))
                    .company(readText(node.path("company")))
                    .duration(readText(node.path("duration")))
                    .description(readText(node.path("description")))
                    .build()));
        }
        return result;
    }
 

  private String readText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        String text = node.asText(null);
        return (text == null || text.isBlank()) ? null : text;
    }

    private String extractJsonObject(String content){
      if(content == null ){
        throw new IllegalStateException("Empty resume parsing content");
      }
      String cleaned = content.replace("```json","").replace("```","").trim();
      int start = cleaned.indexOf("{");
      int end = cleaned.lastIndexOf("}");
      if(start< 0 || end<=start){
        throw new IllegalStateException("No json object to parse content");
      }
      return cleaned.substring(start, end+1);
    }
  private ResumeData emptyResumeData() {
        return ResumeData.builder()
                .skills(new ArrayList<>())
                .projects(new ArrayList<>())
                .experience(new ArrayList<>())
                .education(null)
                .build();
    }

    private String callGroqChat(String userContent){
          String url = groqConfig.getBaseUrl()+"/chat/completions";
          List<Map<String,String>> messages = new ArrayList<>();
          messages.add(Map.of("role","user","content",userContent));
          Map<String,Object> body = Map.of(
                                 "model",groqConfig.getModel(),
                                 "temperature",PARSE_TEMPERATURE,
                                 "response_format",Map.of("type","json_object"),
                                 "messages",messages
          );
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.setBearerAuth(groqConfig.getApiKey());
          HttpEntity<Map<String,Object>> request = new HttpEntity<>(body,headers);
          log.debug("Groq resume-parse request -> model={}", groqConfig.getModel());
          int attempt =0;
          while(true){
            attempt++;
            try{
               ResponseEntity<String> response = groqRestTemplate.postForEntity(url, request, String.class);
                log.debug("Groq resume-parse response (attempt {}): {}", attempt, response.getBody());
                return extractMessageContent(response.getBody());
            }
            catch(HttpServerErrorException | ResourceAccessException ex){
              if(attempt>2){
                log.error("Groq LLM call failed after retry (resume parsing)", ex);
                    throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
              }
              log.warn("Groq LLM call failed (attempt {}), retrying: {}", attempt, ex.getMessage());
            }
            catch(HttpClientErrorException ex){
                log.error("Groq LLM client error {} (resume parsing): {}",
                        ex.getStatusCode(), ex.getResponseBodyAsString());
                        throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
          }
        
    }
  
}
private String extractMessageContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
            }
            return content.asText();
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse Groq chat response envelope", ex);
            throw new BadRequestException("AI service temporarily unavailable. Please try again in a moment.");
        }
    }
}