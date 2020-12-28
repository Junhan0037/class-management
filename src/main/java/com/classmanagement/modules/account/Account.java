package com.classmanagement.modules.account;

import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.main.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account extends BaseTimeEntity implements Serializable {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    private boolean emailVerified;

    private String emailCheckToken;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classroom;

    public void setPassword(String password) {
        this.password = password;
    }

}
