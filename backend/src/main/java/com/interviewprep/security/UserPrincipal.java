package com.interviewprep.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.lang.Collections;

public class UserPrincipal implements UserDetails{
  
  private final Long id;
  private final String email;
  private final String password;
  public UserPrincipal(Long id,String email,String password){
       this.id=id;
       this.email=email;
       this.password=password;
  }
  public Long getId(){
    return id;
  }
  public String  getEmail(){
     return email;
  }
  @Override 
  public Collection<? extends GrantedAuthority> getAuthorities(){
    return Collections.emptyList();
  }
  @Override
  public String getPassword(){
    return password;
  }
  @Override 
  public String getUsername(){
     return email;
  }
   
  @Override 
  public boolean isAccountNonExpired(){
    return true;
  }
@Override 
public boolean isAccountNonLocked(){
  return true;
}
@Override 
public boolean isCredentialsNonExpired(){
  return true;
}
@Override
 public boolean isEnabled(){
  return true;
 }

}
