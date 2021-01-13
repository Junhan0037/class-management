package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountSerializer;
import com.classmanagement.modules.account.AccountsSerializer;
import com.classmanagement.modules.main.BaseTimeEntity;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonSerialize(using = AccountSerializer.class)
    private Account teacher;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.REMOVE)
    @JsonSerialize(using = AccountsSerializer.class)
    private List<Account> members = new ArrayList<>();

    //== 연관관계 메서드 ==//
    public void addMembers(Account account) {
        members.add(account);
        account.setClassroom(this);
    }

    public void setTeacher(Account teacher) {
        this.teacher = teacher;
        teacher.getMyClassroom().add(this);
    }

}
