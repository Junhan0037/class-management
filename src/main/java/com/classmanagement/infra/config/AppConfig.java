package com.classmanagement.infra.config;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
import com.classmanagement.modules.oauth2.OauthClientDetails;
import com.classmanagement.modules.oauth2.OauthClientDetailsRepository;
import com.classmanagement.modules.oauth2.OauthClientDetailsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AppProperties appProperties;
    private final DataSource dataSource;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(appProperties.getSignKey());
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return new ApplicationRunner() {
//
//            @Autowired AccountService accountService;
//            @Autowired AppProperties appProperties;
//            @Autowired OauthClientDetailsRepository oauthClientDetailsRepository;
//            @Autowired PasswordEncoder passwordEncoder;
//
//            @Override
//            public void run(ApplicationArguments args) throws Exception {
//                OauthClientDetails oauthClientDetails = OauthClientDetails.builder()
//                        .clientSecret(passwordEncoder.encode(appProperties.getClientSecret()))
//                        .nonPasswordEncoder(appProperties.getClientSecret())
//                        .scope("read,write")
//                        .autoapprove("read,write")
//                        .authorizedGrantTypes("authorization_code,refresh_token")
//                        .authorities("ROLE_USER")
//                        .accessTokenValidity(24 * 60 * 60)
//                        .refreshTokenValidity(30 * 24 * 60 * 60)
//                        .build();
//                OauthClientDetails savedOauthClient = oauthClientDetailsRepository.save(oauthClientDetails);
//                savedOauthClient.setWebServerRedirectUri("http://localhost:8080/oauth2/callback/" + savedOauthClient.getClientId());
//                OauthClientDetails saveOauthClient = oauthClientDetailsRepository.save(savedOauthClient);
//
//                Account admin = Account.builder()
//                        .email(appProperties.getAdminUsername())
//                        .password(appProperties.getAdminPassword())
//                        .role(Role.ADMIN)
//                        .oauthClientDetails(saveOauthClient)
//                        .build();
//                accountService.saveAccount(admin);
//            }
//        };
//    }

}
