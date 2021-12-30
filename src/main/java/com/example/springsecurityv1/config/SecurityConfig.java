package com.example.springsecurityv1.config;

import com.example.springsecurityv1.config.oauth.PrincipalOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/* sns login 이 완료된 후 처리 필요
 *  1. 코드 받기 -> 사용자 인증 완료( 정상적으로 구글 로그인 완료 )
 *  2. accessToken -> 구글 사용자의 정보에 접근 권한을 가짐 ( 권한 )
 *  3. 사용자의 프로필 정보를 가져옴
 *  4-1. 해당 정보를 토대로 회원가입 자동 진행
 *  4-2. (이메일, 전화번호, 이름, 아이디) 쇼핑몰 ->
 *  */
@Configuration
@EnableWebSecurity  // spring security filter 가 spring filter chain 에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  // Secured annotation 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

    @Autowired
    public SecurityConfig(PrincipalOAuth2UserService principalOAuth2UserService) {
        this.principalOAuth2UserService = principalOAuth2UserService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated()  // 인증 필요
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_NAMEGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .usernameParameter("username")
                .loginProcessingUrl("/login")  // /login 주소가 호출되면 security 가 낚아채서 대신 로그인 진행
                .defaultSuccessUrl("/user")
                .and()
                .oauth2Login()
                .loginPage("/loginForm")  // google login 을 하게 되면 default role 은 USER 이다.
                .defaultSuccessUrl("/user")
                .userInfoEndpoint()  // google login 은 { accessToken + 사용자 프로필 정보 } 같이 받음
                .userService(principalOAuth2UserService);  // DefaultOAuth2UserService 가 들어가야 함 -> 후처리

    }
}
