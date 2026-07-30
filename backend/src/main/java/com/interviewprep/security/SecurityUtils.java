package com.interviewprep.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.interviewprep.exception.UnauthorizedException;

public final class SecurityUtils {
      private SecurityUtils(){

      }
      public static Long getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication ==null || !(authentication.getPrincipal() instanceof UserPrincipal principal)){
          throw new UnauthorizedException("User is not authenticated");
        }
        return principal.getId();

      }
}
