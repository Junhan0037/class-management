package com.classmanagement.modules.oauth2;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OauthToken {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
}
