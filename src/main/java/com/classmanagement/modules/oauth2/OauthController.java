package com.classmanagement.modules.oauth2;

import com.google.gson.Gson;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/oauth2", produces = MediaTypes.HAL_JSON_VALUE)
public class OauthController {

    private final RestTemplate restTemplate;
    private final Gson gson;
    private final OauthClientDetailsRepository oauthClientDetailsRepository;
    private final OauthTokenRepository oauthTokenRepository;

    @GetMapping()
    public CodeResponse token(@RequestParam String code) {
        return new CodeResponse(code);
    }

    @Data
    static class CodeResponse {
        private String authorization_code;
        public CodeResponse(String code) {
            this.authorization_code = code;
        }
    }

    @GetMapping("/token")
    public OauthToken callAccessToken(@RequestParam String code, @RequestParam("grant_type") String grantType, @RequestParam("client_id") Long id) {
        OauthClientDetails oauthClientDetails = oauthClientDetailsRepository.findByClientId(id).get();
        String clientId = String.valueOf(oauthClientDetails.getClientId());
        String clientPassword = oauthClientDetails.getNonPasswordEncoder();

        String credentials = clientId + ":" + clientPassword;
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", grantType);
        params.add("redirect_uri", "http://localhost:8080/oauth2");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/oauth/token", request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            OauthToken oauthToken = gson.fromJson(response.getBody(), OauthToken.class);
            oauthToken.setOauthClientDetails(oauthClientDetails);
            return oauthTokenRepository.save(oauthToken);
        }
        return null;
    }

}
