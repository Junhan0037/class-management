package com.classmanagement.modules.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    TEACHER("ROLE_TEACHER", "선생님"),
    STUDENT("ROLE_STUDENT", "학생"),
    USER("ROLE_USER", "일반사용자");

    private final String key;
    private final String title;

}
