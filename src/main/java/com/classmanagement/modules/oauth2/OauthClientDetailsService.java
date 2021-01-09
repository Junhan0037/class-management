package com.classmanagement.modules.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OauthClientDetailsService {

    private final OauthClientDetailsRepository oauthClientDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    public OauthClientDetails createOauthClientDetails() {
        String password = UUID.randomUUID().toString();
        OauthClientDetails oauthClientDetails = OauthClientDetails.builder()
                .clientSecret(passwordEncoder.encode(password))
                .nonPasswordEncoder(password)
                .scope("read,write")
                .autoapprove("read,write")
                .authorizedGrantTypes("authorization_code,refresh_token")
                .authorities("ROLE_USER")
                .accessTokenValidity(24 * 60 * 60)
                .refreshTokenValidity(30 * 24 * 60 * 60)
                .webServerRedirectUri("http://localhost:8080/oauth2")
                .build();
        return oauthClientDetailsRepository.save(oauthClientDetails);
    }

}
