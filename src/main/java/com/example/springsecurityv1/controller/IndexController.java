package com.example.springsecurityv1.controller;

import com.example.springsecurityv1.config.auth.PrincipalDetails;
import com.example.springsecurityv1.model.User;
import com.example.springsecurityv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller  // return View
public class IndexController {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public IndexController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // @AuthenticationPrincipal: 해당 annotation 을 통해 session 에 접근할 수 있다.
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails) {
        try {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();  // down casting
            System.out.println("authentication: " + principalDetails.getUser());
            System.out.println("getUser: " +  userDetails.getUser());
            System.out.println("attributes: " + userDetails.getAttributes());

            return "check session info";
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR";
        }
    }
    // PrincipalDetails 에서 OAuth2User 를 implements 했기 때문에 PrincipalDetails 객체로 받으면 됨! (자동 casting)
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails oAuth2User) {
         try {
             OAuth2User oAuth2 = (OAuth2User) authentication.getPrincipal();  // down casting
             System.out.println("getAuthorities: " + oAuth2.getAuthorities());
             System.out.println("getUser: " +  oAuth2User.getUser());
             System.out.println("attributes: " + oAuth2User.getAttributes());

             return "check OAuth info";
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR";
        }
    }

    @GetMapping({"", "/"})
    public String index(){
        // mustache default folder: src/main/resource
        // templates (prefix), mustache (suffix) -> 설정 생략 가능
        return "index";  // default: index.mustache
    }

    /* @AuthenticationPrincipal 이 활성화되는 시점
    *
    * */
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println(principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // Spring security 가 해당 주소를 낚아챔 -> SecurityConfig 파일 생성 후 작동 안 함!
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");

        // encoding password
        String encPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encPassword);

        userRepository.save(user);
        // password 암호화가 안 되어 있으면 security login 을 할 수 없다!
        return "redirect:/loginForm";
    }

    // 특정 method (api)에 Security 를 걸고 싶을 때
    @Secured("ROLE_ADMIN")  // 권한 없으면 403 ERROR!!
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "user info";
    }

    // data method 실행되기 직전에 실행 됨
    // @PostAuthorize 는 잘 안 씀
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "user data";
    }
}
