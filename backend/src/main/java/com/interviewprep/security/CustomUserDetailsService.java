package com.interviewprep.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.interviewprep.modules.auth.entity.User;
import com.interviewprep.modules.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
private UserRepository userRepository;

@Override
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
  
User user =userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("username not found with this email"+email));

return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword());
}
  
}
