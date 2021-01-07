package com.classmanagement.modules.oauth2;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OauthToken {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 1000)
    private String access_token;

    private String token_type;

    @Column(length = 1000)
    private String refresh_token;

    private Integer expires_in;

    private String scope;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OauthClientDetailsSerializer.class)
    private OauthClientDetails oauthClientDetails;

}
