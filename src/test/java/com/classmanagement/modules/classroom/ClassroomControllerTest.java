package com.classmanagement.modules.classroom;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountRepository;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class ClassroomControllerTest extends BaseTest {

    @Autowired AppProperties appProperties;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired OauthClientDetailsService oauthClientDetailsService;
    @Autowired ClassroomService classroomService;
    @Autowired ClassroomRepository classroomRepository;

    @BeforeEach
    public void setup() {
        classroomRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("정상적으로 학급 생성하는 테스트")
    public void createClassroom() throws Exception {
        Map<String, Object> map = createAccount();
        Object oauthClientDetails = map.get("oauthClientDetails");

        ClassroomDto classroomDto = ClassroomDto.builder()
                                                .name("병아리반")
                                                .build();

        mockMvc.perform(post("/api/classroom")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken((OauthClientDetails) oauthClientDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(classroomDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value("병아리반"))
                .andExpect(jsonPath("teacher.name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-classroom").exists())
                .andDo(document("create-classroom",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("create-classroom").description("link to query-classroom"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT token header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new classroom")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("createdDate").description("CreatedDate of new classroom"),
                                fieldWithPath("lastModifiedDate").description("LastModifiedDate of new classroom"),
                                fieldWithPath("id").description("Id of new classroom"),
                                fieldWithPath("name").description("Name of new classroom"),
                                fieldWithPath("teacher.name").description("Name of teacher"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.create-classroom.href").description("link to query-classroom"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    @Test
    @DisplayName("입력 받을 수 없는 값을 사용한 경우에 에러를 발생하는 테스트")
    public void saveClassroom_Bad_Request() throws Exception {
        Map<String, Object> map = createAccount();
        Object oauthClientDetails = map.get("oauthClientDetails");

        Classroom classroom = Classroom.builder()
                .id(123l) // 받을 수 없는 입력값
                .name("병아리반")
                .build();

        mockMvc.perform(post("/api/classroom")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken((OauthClientDetails) oauthClientDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(classroom)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우에 에러를 발생하는 테스트")
    public void createClassroom_Bad_Request_Empty_Input() throws Exception {
        Map<String, Object> map = createAccount();
        Object oauthClientDetails = map.get("oauthClientDetails");

        ClassroomDto classroomDto = ClassroomDto.builder().build(); // 비어있는 입력값

        mockMvc.perform(post("/api/classroom")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken((OauthClientDetails) oauthClientDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(classroomDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("관리하는 학급 목록 조회 테스트")
    public void queryClassrooms() throws Exception {
        Map<String, Object> map = createAccount();
        Object account = map.get("account");
        Object oauthClientDetails = map.get("oauthClientDetails");

        IntStream.range(0, 30).forEach(x -> this.generateClassroom((Account) account, x));

        mockMvc.perform(get("/api/classroom")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken((OauthClientDetails) oauthClientDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("page", "0")
                        .param("size", "3")
                        .param("sort", "id"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("_embedded.classroomList").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.query-classrooms").exists())
                .andDo(document("query-classrooms",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-classrooms").description("link to query-classrooms"),
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
                                fieldWithPath("_embedded.classroomList[0].createdDate").description("CreatedDate of new classroom"),
                                fieldWithPath("_embedded.classroomList[0].lastModifiedDate").description("LastModifiedDate of new classroom"),
                                fieldWithPath("_embedded.classroomList[0].id").description("Id of new classroom"),
                                fieldWithPath("_embedded.classroomList[0].name").description("Name of new classroom"),
                                fieldWithPath("_embedded.classroomList[0].teacher.name").description("Teacher of new classroom"),
                                fieldWithPath("_embedded.classroomList[0]._links.self.href").description("link to self"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-classrooms.href").description("link to query-classrooms"),
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

    private Map<String, Object> createAccount() {
        Map<String, Object> map = new HashMap<>();

        Account account = Account.builder()
                .email(appProperties.getTestUsername())
                .password(appProperties.getTestPassword())
                .name("홍길동")
                .role(Role.TEACHER)
                .build();

        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        Account saveAccount = accountService.saveAccount(account);

        map.put("account", saveAccount);
        map.put("oauthClientDetails", oauthClientDetails);

        return map;
    }

    private String getBearerToken(OauthClientDetails oauthClientDetails) throws Exception {
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

    private Classroom generateClassroom(Account account, int index) {
        Classroom classroom = Classroom.builder()
                .name("병아리반" + index)
                .teacher(account)
                .build();
        return classroomService.saveClassroom(classroom);
    }

}