package com.example.springsecurityv1.config.oauth;

import com.example.springsecurityv1.config.auth.PrincipalDetails;
import com.example.springsecurityv1.model.User;
import com.example.springsecurityv1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;

    @Autowired
    public PrincipalOAuth2UserService(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    /* loadUser
    * google 로 부터 받은 userRequest 데이터에 대한 후처리 method
    * 해당 method 종료 시 @AuthenticationPrincipal annotation 이 만들어진다!!
    * */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        printOAuthRequest(userRequest);

        System.out.println("OAuth 회원가입");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("getAttributes: " + oAuth2User.getAttributes());
        /*
        * 1. google login button
        * 2. google login 창
        * 3. login 완료
        * 4. return code(OAuth-Client library) -> AccessToken 요청 -> AccessToken 받음 (OAuth2UserRequest)
        * 5. loadUser method 호출 -> user profile info 받아줌
        * */
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");

        // user 확인
        String username = provider+"_"+providerId;
        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode("chlwodms"))
                    .email(oAuth2User.getAttribute("email"))
                    .role("ROLE_USER")
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        }

        // Authentication 객체 안으로 들어감!
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }

    private void printOAuthRequest(OAuth2UserRequest userRequest) {
        System.out.println("OAuthUserRequest: " + userRequest);  // org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest@661a7954
        System.out.println("AccessToken Value: " + userRequest.getAccessToken().getTokenValue());
        System.out.println("Additional Params: " + userRequest.getAdditionalParameters());  // id_token
        // registration, clientId, clientSecret, clientAuthenticationMethod, authorizationGrantType, redirectUri, scopes, providerDetails, clientName
        System.out.println("Client Registration's client id: " + userRequest.getClientRegistration().getRegistrationId());  // registrationId='google'
        System.out.println("-----------------------------------------------------");
        System.out.println("super.loadUser(userRequest): " + super.loadUser(userRequest));
        System.out.println("attributes: " + super.loadUser(userRequest).getAttributes());  // 사용자 정보 처리하려면 attributes 필요!!
        System.out.println("name: " + super.loadUser(userRequest).getName());

        /* attributes:
         * { sub=101302635592684472554,
         *   name=최재은,
         *   given_name=재은,
         *   family_name=최,
         *   picture=https://lh3.googleusercontent.com/a/AATXAJzNie9ThyeuCxS9OaQS8c6_1Ffx7VpmC6akt0E_=s96-c,
         *   email=poungki990823@gmail.com,
         *   email_verified=true,
         *   locale=ko }
         * */
    }

}
