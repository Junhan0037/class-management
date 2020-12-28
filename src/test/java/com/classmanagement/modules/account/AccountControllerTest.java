package com.classmanagement.modules.account;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.common.BaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends BaseTest {

    @Autowired AppProperties appProperties;

    @Test
    @DisplayName("회원 가입")
    public void saveAccount() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email(appProperties.getStudentUsername())
                .password(appProperties.getStudentPassword())
                .passwordConfirm(appProperties.getStudentPassword())
                .name("이동현")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("email").value(appProperties.getStudentUsername()))
                .andExpect(jsonPath("name").value("이동현"));
    }

}