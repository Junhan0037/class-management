package com.classmanagement.modules.account;

import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.main.BaseTimeEntity;
import com.classmanagement.modules.oauth2.OauthClientDetails;
import com.classmanagement.modules.oauth2.OauthClientDetailsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Account extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classroom;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OauthClientDetailsSerializer.class)
    private OauthClientDetails oauthClientDetails;

}
