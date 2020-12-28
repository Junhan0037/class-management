package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.main.BaseTimeEntity;
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

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    List<Account> accounts = new ArrayList<>();

}
