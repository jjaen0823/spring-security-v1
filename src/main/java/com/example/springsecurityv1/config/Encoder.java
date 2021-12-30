package com.example.springsecurityv1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class Encoder {
    // 해당 method 의 return object 를 IoC 로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePW() {
        return new BCryptPasswordEncoder();
    }
}
