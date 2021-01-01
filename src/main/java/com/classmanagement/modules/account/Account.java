package com.classmanagement.modules.account;

import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.main.BaseTimeEntity;
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
    private Role role = Role.USER;

    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classroom;

    private String AuthorizationID;
    private String AuthorizationPW;

    public void setPassword(String password) {
        this.password = password;
    }

}
