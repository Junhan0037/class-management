package com.classmanagement.modules.oauth2;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/oauth")
public class OauthClientDetailsController {

    private final ModelMapper modelMapper;
    private final OauthClientDetailsRepository oauthClientDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/client")
    public OauthClientDetails createOauthClientDetails(OauthClientDetailsDto oauthClientDetailsDto) {
        OauthClientDetails oauthClientDetails = modelMapper.map(oauthClientDetailsDto, OauthClientDetails.class);
        oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetailsDto.getClientSecret()));
        oauthClientDetails.setScope("read,write");
        oauthClientDetails.setAutoapprove("read,write");
        oauthClientDetails.setAuthorizedGrantTypes("authorization_code,refresh_token");
        oauthClientDetails.setWebServerRedirectUri("http://localhost:8080/oauth2/callback2");
        oauthClientDetails.setAuthorities("ROLE_USER");
        oauthClientDetails.setAccessTokenValidity(24 * 60 * 60);
        oauthClientDetails.setRefreshTokenValidity(30 * 24 * 60 * 60);
        return oauthClientDetailsRepository.save(oauthClientDetails);
    }

}
