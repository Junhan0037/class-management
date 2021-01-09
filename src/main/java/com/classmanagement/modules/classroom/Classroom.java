package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountSerializer;
import com.classmanagement.modules.main.BaseTimeEntity;
import com.classmanagement.modules.oauth2.OauthClientDetailsSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Classroom extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonSerialize(using = AccountSerializer.class)
    private Account teacher;

    @JsonIgnore
    @OneToMany(mappedBy = "classroom")
    List<Account> members = new ArrayList<>();

    //== 연관관계 메서드 ==//
    public void addMembers(Account account) {
        members.add(account);
        account.setClassroom(this);
    }

}
