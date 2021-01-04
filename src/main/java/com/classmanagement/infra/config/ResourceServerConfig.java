package com.classmanagement.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private final TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("account");
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous()
            .and()
            .authorizeRequests()
                .mvcMatchers("oauth/**", "/oauth2/callback").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api").permitAll() // IndexController
                .mvcMatchers(HttpMethod.POST, "/api/accounts").permitAll() // 회원 등록
                .anyRequest().authenticated();
        http.exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

}
