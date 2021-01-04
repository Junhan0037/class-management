package com.classmanagement.modules.oauth2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class OauthClientDetailsDto {

    private String clientId;

    private String clientSecret;

}
