package com.classmanagement.modules.oauth2;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "oauth_client_details")
public class OauthClientDetails {

    @Id @GeneratedValue
    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "resource_ids")
    private String resourceIds;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "scope")
    private String scope;

    @Column(name = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Column(name = "web_server_redirect_uri")
    private String webServerRedirectUri ;

    @Column(name = "authorities")
    private String authorities;

    @Column(name = "access_token_validity")
    private Integer accessTokenValidity ;

    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValidity ;

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "autoapprove")
    private String autoapprove;

    private String nonPasswordEncoder;

}
