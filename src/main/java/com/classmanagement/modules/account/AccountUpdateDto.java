package com.classmanagement.modules.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class AccountUpdateDto {

//    @NotEmpty
//    @Length(min = 3, max = 20)
//    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
//    private String name;

    @NotNull
    private Job job;

}
