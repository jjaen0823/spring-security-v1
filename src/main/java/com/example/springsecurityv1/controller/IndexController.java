package com.example.springsecurityv1.controller;

import com.example.springsecurityv1.model.User;
import com.example.springsecurityv1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller  // return View
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping({"", "/"})
    public String index(){
        // mustache default folder: src/main/resource
        // templates (prefix), mustache (suffix) -> 설정 생략 가능
        return "index";  // default: index.mustache
    }

    @GetMapping("/user")
    public @ResponseBody String user() {
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

    @Secured("ROLE_ADMIN")  // 권한 없으면 403 ERROR!!
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "user info";
    }
}
