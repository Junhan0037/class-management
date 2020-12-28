package com.classmanagement.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @ResponseBody
    @GetMapping("/api/accounts")
    public List<Account> test() {
        List<Account> accounts = accountService.showAccount();
        return accounts;
    }

}
