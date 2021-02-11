package com.classmanagement.modules.account;

import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.main.BaseTimeEntity;
import com.classmanagement.modules.oauth2.OauthClientDetails;
import com.classmanagement.modules.oauth2.OauthClientDetailsSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Account extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String name;

    private Integer money = 0;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Job job = Job.PUBLIC;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = OauthClientDetailsSerializer.class)
    private OauthClientDetails oauthClientDetails;

    @OneToMany(mappedBy = "teacher")
    @JsonIgnore
    private List<Classroom> myClassroom = new ArrayList<>(); // 선생님의 학급

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Classroom classroom; // 학생들의 학급

    //== 연관관계 메서드 ==//
    public void setOauthClientDetails(OauthClientDetails oauthClientDetails) {
        this.oauthClientDetails = oauthClientDetails;
        oauthClientDetails.setAccount(this);
    }

}
