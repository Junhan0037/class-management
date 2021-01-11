package com.classmanagement.modules.account;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.common.BaseTest;
import com.classmanagement.modules.oauth2.OauthClientDetails;
import com.classmanagement.modules.oauth2.OauthClientDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest extends BaseTest {

    @Autowired AppProperties appProperties;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired OauthClientDetailsService oauthClientDetailsService;

    @BeforeEach
    public void setup() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 회원 가입하는 테스트")
    public void createAccount() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .passwordConfirm(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("role").value(Role.STUDENT.name()))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-account").exists())
                .andDo(document("create-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-account").description("link to query-accounts"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("Email of new account"),
                                fieldWithPath("password").description("Password of new account"),
                                fieldWithPath("passwordConfirm").description("PasswordConfirm of new account"),
                                fieldWithPath("name").description("Name of new account"),
                                fieldWithPath("role").description("Role of new account (TEACHER, STUDENT)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("CreatedDate of new account"),
                                fieldWithPath("lastModifiedDate").description("LastModifiedDate of new account"),
                                fieldWithPath("id").description("Id of new account"),
                                fieldWithPath("email").description("Email of new account"),
                                fieldWithPath("password").description("Password of new account"),
                                fieldWithPath("name").description("Name of new account"),
                                fieldWithPath("role").description("Role of new account"),
                                fieldWithPath("oauthClientDetails.REST_API_KEY").description("Id of OauthClient"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-account.href").description("link to query-accounts"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러를 발생하는 테스트")
    public void saveAccount_Bad_Request() throws Exception {
        Account account = Account.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.STUDENT)
                .id(123l) // 받을 수 없는 입력값
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(account)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 에러를 발생하는 테스트")
    public void createAccount_Bad_Request_Empty_Input() throws Exception {
        AccountDto accountDto = AccountDto.builder().build(); // 비어있는 입력값

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("입력값이 잘못된 경우에 에러를 발생하는 테스트")
    public void createAccount_Bad_Request_Wrong_Input() throws Exception {
        AccountDto accountDto = AccountDto.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .passwordConfirm("skjdiwhduadas")
                .name("홍길동")
                .role(Role.STUDENT)
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("정상적으로 회원 목록 조회 테스트")
    public void queryAccounts() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateAccount);

        mockMvc.perform(get("/api/accounts")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("_embedded.accountList").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-accounts").exists())
                .andDo(document("query-accounts",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-accounts").description("link to query-accounts"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT token header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.accountList[0].createdDate").description("CreatedDate of new account"),
                                fieldWithPath("_embedded.accountList[0].lastModifiedDate").description("LastModifiedDate of new account"),
                                fieldWithPath("_embedded.accountList[0].id").description("Id of new account"),
                                fieldWithPath("_embedded.accountList[0].email").description("Email of new account"),
                                fieldWithPath("_embedded.accountList[0].password").description("Password of new account"),
                                fieldWithPath("_embedded.accountList[0].name").description("Name of new account"),
                                fieldWithPath("_embedded.accountList[0].role").description("Role of new account"),
                                fieldWithPath("_embedded.accountList[0].oauthClientDetails.REST_API_KEY").description("Id of OauthClient"),
                                fieldWithPath("_embedded.accountList[0]._links.self.href").description("link to self"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-accounts.href").description("link to query-accounts"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.first.href").description("link to first page"),
                                fieldWithPath("_links.next.href").description("link to next page"),
                                fieldWithPath("_links.last.href").description("link to last page"),
                                fieldWithPath("page.size").description("한 페이지에 보여줄 사이즈(갯수)"),
                                fieldWithPath("page.totalElements").description("모든 요소"),
                                fieldWithPath("page.totalPages").description("전체 페이지 수"),
                                fieldWithPath("page.number").description("현재 페이지(0부터)")
                        )
                ));
    }

    @Test
    @DisplayName("정상적으로 회원 단건 조회 테스트")
    public void queryAccount() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateAccount);

        mockMvc.perform(get("/api/accounts/{email}", appProperties.getTestUsername() + 10)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("email").value(appProperties.getTestUsername() + 10))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-account").exists())
                .andDo(document("query-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-account").description("link to query-account"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT token header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("CreatedDate of new account"),
                                fieldWithPath("lastModifiedDate").description("LastModifiedDate of new account"),
                                fieldWithPath("id").description("Id of new account"),
                                fieldWithPath("email").description("Email of new account"),
                                fieldWithPath("password").description("Password of new account"),
                                fieldWithPath("name").description("Name of new account"),
                                fieldWithPath("role").description("Role of new account"),
                                fieldWithPath("oauthClientDetails.REST_API_KEY").description("Id of OauthClient"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-account.href").description("link to query-account"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("30개의 회원정보를 10개씩 첫번째 페이지 조회하기")
    public void queryAccountsWithPaging() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateAccount);

        //when & then
        mockMvc.perform(get("/api/accounts")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.accountList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-accounts").exists())
                .andExpect(jsonPath("_links.first").exists())
                .andExpect(jsonPath("_links.next").exists())
                .andExpect(jsonPath("_links.last").exists());
    }

    @Test
    @DisplayName("없는 회원정보를 조회했을 때 404 응답받기")
    public void getAccount404() throws Exception {
        //when & then
        mockMvc.perform(get("/api/accounts/abc@abc.com")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("회원정보를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //given
        Account account = Account.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.ADMIN)
                .build();

        AccountUpdateDto accountUpdateDto = modelMapper.map(account, AccountUpdateDto.class);
        String newName = "김삿갓";
        accountUpdateDto.setName(newName);

        mockMvc.perform(put("/api/accounts/{email}", appProperties.getTestUsername())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(newName))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.update-account").exists())
                .andDo(document("update-account",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("update-account").description("link to update-account"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT token header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("새로운 이름")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("CreatedDate of new account"),
                                fieldWithPath("lastModifiedDate").description("LastModifiedDate of new account"),
                                fieldWithPath("id").description("Id of new account"),
                                fieldWithPath("email").description("Email of new account"),
                                fieldWithPath("password").description("Password of new account"),
                                fieldWithPath("name").description("Name of new account"),
                                fieldWithPath("role").description("Role of new account"),
                                fieldWithPath("oauthClientDetails.REST_API_KEY").description("Id of OauthClient"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-account.href").description("link to update-account"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 회원 수정 실패")
    public void updateAccount_Bad_Request_Empty_Input() throws Exception {
        AccountUpdateDto accountUpdateDto = AccountUpdateDto.builder().build(); // 비어있는 입력값

        mockMvc.perform(put("/api/accounts/{email}", appProperties.getTestUsername())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("해당 회원이 존재하지 않을 경우 회원 수정 실패")
    public void updateAccount_Bad_Request_Wrong_Input() throws Exception {
        //given
        Account account = Account.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.ADMIN)
                .build();

        AccountUpdateDto accountUpdateDto = modelMapper.map(account, AccountUpdateDto.class);
        String newName = "김삿갓";
        accountUpdateDto.setName(newName);

        mockMvc.perform(put("/api/accounts/abc@abc.com")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(accountUpdateDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private String getBearerToken() throws Exception {
        Account account = Account.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.ADMIN)
                .build();
        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        accountService.saveAccount(account);

        ResultActions authorize = mockMvc.perform(get("/oauth/authorize")
                                                    .with(httpBasic(appProperties.getTestUsername(), appProperties.getTestPassword()))
                                                    .param("client_id", String.valueOf(oauthClientDetails.getClientId()))
                                                    .param("redirect_uri", "http://localhost:8080/oauth2")
                                                    .param("response_type", "code"));
        var redirectedUrl = authorize.andReturn().getResponse().getRedirectedUrl();
        String code = redirectedUrl.substring(redirectedUrl.lastIndexOf("=") + 1);

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                                                .with(httpBasic(String.valueOf(oauthClientDetails.getClientId()), oauthClientDetails.getNonPasswordEncoder()))
                                                .param("code", code)
                                                .param("grant_type", "authorization_code")
                                                .param("redirect_uri", "http://localhost:8080/oauth2"));
        var resultBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        String accessToken = parser.parseMap(resultBody).get("access_token").toString();

        return "Bearer " + accessToken;
    }

    private Account generateAccount(int index) {
        Account account = Account.builder()
                                .email(appProperties.getTestUsername() + index)
                                .password(appProperties.getTestPassword() + index)
                                .name("홍길동" + index)
                                .role(Role.ADMIN)
                                .build();
        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        return accountRepository.save(account);
    }

}