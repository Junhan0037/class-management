package com.classmanagement.modules.oauth2;

import com.classmanagement.modules.main.BaseTimeEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OauthToken extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 1000)
    private String access_token;

    private String token_type;

    @Column(length = 1000)
    private String refresh_token;

    private Integer expires_in;

    private String scope;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OauthClientDetailsSerializer.class)
    private OauthClientDetails oauthClientDetails;

    public void setOauthClientDetails(OauthClientDetails oauthClientDetails) {
        this.oauthClientDetails = oauthClientDetails;
        oauthClientDetails.getOauthToken().add(this);
    }

}
