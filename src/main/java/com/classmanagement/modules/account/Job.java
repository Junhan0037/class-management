package com.classmanagement.modules.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Job {

    PUBLIC("PUBLIC", "무직"),
    BANK("BANK", "은행원"),
    GOVERNMENT("GOVERNMENT", "정부");

    private final String key;
    private final String title;

}
