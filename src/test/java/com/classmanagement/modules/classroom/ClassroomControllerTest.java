package com.classmanagement.modules.classroom;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountRepository;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
import com.classmanagement.modules.common.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class ClassroomControllerTest extends BaseTest {

    @Autowired AccountService accountService;
    @Autowired AppProperties appProperties;
    @Autowired AccountRepository accountRepository;
    @Autowired ClassroomRepository classroomRepository;

    @BeforeEach
    public void setup() { // Test마다 인메모리 DB 정리
        classroomRepository.deleteAll();
        accountRepository.deleteAll();
    }

    private String getBearerToken(boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception {
        // Given
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getAdminUsername())
                .param("password", appProperties.getAdminPassword())
                .param("grant_type", "password"));

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        Account junhan = Account.builder()
                .email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(Role.ADMIN, Role.TEACHER))
                .build();
        return accountService.saveAccount(junhan);
    }

}