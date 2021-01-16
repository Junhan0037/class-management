package com.classmanagement.modules.government;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.modules.account.*;
import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.classroom.ClassroomRepository;
import com.classmanagement.modules.classroom.ClassroomService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class GovernmentControllerTest extends BaseTest {

    @Autowired AppProperties appProperties;
    @Autowired OauthClientDetailsService oauthClientDetailsService;
    @Autowired AccountService accountService;
    @Autowired ClassroomService classroomService;
    @Autowired ClassroomRepository classroomRepository;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    public void setup() {
        classroomRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("자신이 속한 학급 자산 통계 테스트")
    public void createClassroom() throws Exception {
        // 선생님 생성
        Account teacher = createTeacher();

        // 학급 생성
        Classroom classroom = createClassroom(teacher);

        // 정부 생성 및 학급 참여
        Map<String, Object> map = createGovernment(classroom);
        Account government = (Account) map.get("account");
        OauthClientDetails oauthClientDetails = (OauthClientDetails) map.get("oauthClientDetails");

        // 학생 30명 생성 및 학급 참여
        IntStream.range(1, 31).forEach(x -> this.generateAccount(x, classroom));

        mockMvc.perform(get("/api/government/money")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken(oauthClientDetails, "government@email.com", "1234"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("sum").exists())
                .andExpect(jsonPath("average").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.statistics-classroom").exists());
    }

    private Map<String, Object> createGovernment(Classroom classroom) {
        Map<String, Object> map = new HashMap<>();

        Account account = Account.builder()
                                .email("government@email.com")
                                .password("1234")
                                .name("정부")
                                .role(Role.STUDENT)
                                .job(Job.GOVERNMENT)
                                .classroom(classroom)
                                .build();

        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        Account saveAccount = accountService.saveAccount(account);

        map.put("account", saveAccount);
        map.put("oauthClientDetails", oauthClientDetails);

        return map;
    }

    private Account createTeacher() {
        Account account = Account.builder()
                                .email(appProperties.getTestUsername())
                                .password(appProperties.getTestPassword())
                                .name("선생님")
                                .role(Role.TEACHER)
                                .build();

        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        return accountService.saveAccount(account);
    }

    private Account generateAccount(int index, Classroom classroom) {
        Account account = Account.builder()
                                .email(appProperties.getTestUsername() + index)
                                .password(appProperties.getTestPassword())
                                .name("학생" + index)
                                .money(index * 1000)
                                .role(Role.STUDENT)
                                .job(Job.PUBLIC)
                                .classroom(classroom)
                                .build();
        OauthClientDetails oauthClientDetails = oauthClientDetailsService.createOauthClientDetails();
        account.setOauthClientDetails(oauthClientDetails);
        return accountService.saveAccount(account);
    }

    private Classroom createClassroom(Account account) {
        Classroom classroom = Classroom.builder()
                                        .name("병아리반")
                                        .teacher(account)
                                        .members(new ArrayList<>())
                                        .build();
        return classroomService.saveClassroom(classroom);
    }

    private String getBearerToken(OauthClientDetails oauthClientDetails, String id, String pw) throws Exception {
        ResultActions authorize = mockMvc.perform(get("/oauth/authorize")
                .with(httpBasic(id, pw))
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

}