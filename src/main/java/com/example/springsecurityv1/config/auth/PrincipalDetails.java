package com.example.springsecurityv1.config.auth;

// security 가 /login 낚아채서 로그인 진행
// 완료 시, session 을 만들어 줌!! (security session 공간 -> Security ContextHolder)
// 저 공간에 들어갈 수 있는 object 는 정해져있음 -> Authentication object type
// Authentication 안에 User 정보가 있어야 됨.
// User object type => UserDetails object type 이어야 함

import com.example.springsecurityv1.model.User;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// Security Session [ Authentication [ UserDetails [ User ] ] ]
@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;  // composition
    private Map<String, Object> attributes;

    // 일반 login
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth login
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // User 의 권한 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();  // ArrayList 는 Collection 의 자식
        collection.add((GrantedAuthority) () -> user.getRole());
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 1 년 동안 로그인 안 하면 휴면 계정으로 만든다는 조항이 있으면
        // private TimeStamp loginDate 생성
        // currentTime - loginDate => 1년 초과 시 return false;

        return true;
    }

    @Override
    public String getName() {
        return (String) attributes.get("sub");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
