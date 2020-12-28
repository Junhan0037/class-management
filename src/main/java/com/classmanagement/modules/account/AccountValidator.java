package com.classmanagement.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class AccountValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        AccountDto accountDto = (AccountDto) object;
        if (!accountDto.getPassword().equals(accountDto.getPasswordConfirm())) {
            errors.rejectValue("password", "invalid.password", new Object[]{accountDto.getPassword()}, "비밀번호가 틀립니다.");
        }
        if (accountRepository.existsByEmail(accountDto.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{accountDto.getEmail()}, "이미 사용중인 이메일입니다.");
        }
    }

}
