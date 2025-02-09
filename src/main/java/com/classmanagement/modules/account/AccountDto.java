package com.classmanagement.modules.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class AccountDto {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Length(min = 4, max = 50)
    private String password;

    @NotEmpty
    @Length(min = 4, max = 50)
    private String passwordConfirm;

    @NotNull
    private Role role;

    @NotEmpty
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String name;

}
