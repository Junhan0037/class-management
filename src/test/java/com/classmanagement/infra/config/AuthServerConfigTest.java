package com.classmanagement.infra.config;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.common.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseTest {

    @Autowired AccountService accountService;
    @Autowired AppProperties appProperties;

    @Test
    @DisplayName("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                        .param("username", appProperties.getAdminUsername())
                        .param("password", appProperties.getAdminPassword())
                        .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());
    }

}