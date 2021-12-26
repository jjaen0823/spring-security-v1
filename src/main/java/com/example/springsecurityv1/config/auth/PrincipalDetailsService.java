package com.example.springsecurityv1.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Security 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService type 으로 IoC 되어있는 loadUserByUsername method 실행

@Service  // IoC 등록
public class PrincipalDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // parameter 로 username 을 사용하려면 input name 이 username 으로 동일해야 함.
        // input user name 변경해서 사용하려면 security config 에서 .usernameParameter("") 로 설정해줘야 함.
        return null;
    }
}