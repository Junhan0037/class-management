package com.classmanagement.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final AccountValidator accountValidator;

    @InitBinder("accountDto")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountValidator);
    }

    @ResponseBody
    @PostMapping("/api/accounts")
    public Account saveAccount(@Valid @RequestBody AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) {
            return null;
        }
        Account account = modelMapper.map(accountDto, Account.class);
        return accountService.saveAccount(account);
    }

}
