package com.classmanagement.infra.config;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.AccountDto;
import com.classmanagement.modules.account.AccountRepository;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
import com.classmanagement.modules.classroom.ClassroomRepository;
import com.classmanagement.modules.common.BaseTest;
import com.classmanagement.modules.oauth2.OauthClientDetails;
import com.classmanagement.modules.oauth2.OauthClientDetailsRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OauthServerConfigTest extends BaseTest {

    @Autowired AccountService accountService;
    @Autowired AppProperties appProperties;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired OauthClientDetailsRepository oauthClientDetailsRepository;
    @Autowired ClassroomRepository classroomRepository;
    
    @BeforeEach
    public void setup() {
        classroomRepository.deleteAll();
        accountRepository.deleteAll();
    }
    
    @Test
    @DisplayName("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                                        .email(appProperties.getTestUsername())
                                        .password(appProperties.getTestPassword())
                                        .passwordConfirm(appProperties.getTestPassword())
                                        .name("홍길동")
                                        .role(Role.STUDENT)
                                        .build();

        ResultActions accounts = mockMvc.perform(post("/api/accounts")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaTypes.HAL_JSON)
                                                .content(objectMapper.writeValueAsString(accountDto)));

        var responseBody = accounts.andReturn().getResponse().getContentAsString();
        JSONObject jsonObject = new JSONObject(responseBody);
        String id = jsonObject.getJSONObject("oauthClientDetails").getString("REST_API_KEY");

        ResultActions authorize = mockMvc.perform(get("/oauth/authorize")
                                                .with(httpBasic(appProperties.getTestUsername(), appProperties.getTestPassword()))
                                                .param("client_id", id)
                                                .param("redirect_uri", "http://localhost:8080/oauth2")
                                                .param("response_type", "code"));

        var redirectedUrl = authorize.andReturn().getResponse().getRedirectedUrl();
        String code = redirectedUrl.substring(redirectedUrl.lastIndexOf("=") + 1);

        OauthClientDetails oauthClientDetails = oauthClientDetailsRepository.findByClientId(Long.valueOf(id)).get();
        String clientId = String.valueOf(oauthClientDetails.getClientId());
        String clientPassword = oauthClientDetails.getNonPasswordEncoder();

        // OauthController의 "/oauth2/token" connect 문제로 바로 토큰 받는 방식으로 테스트코드 구현
        mockMvc.perform(post("/oauth/token")
                        .with(httpBasic(clientId, clientPassword))
                        .param("code", code)
                        .param("grant_type", "authorization_code")
                        .param("redirect_uri", "http://localhost:8080/oauth2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("token_type").exists())
                .andExpect(jsonPath("refresh_token").exists())
                .andExpect(jsonPath("expires_in").exists())
                .andExpect(jsonPath("scope").exists())
                .andExpect(jsonPath("jti").exists());
    }

}