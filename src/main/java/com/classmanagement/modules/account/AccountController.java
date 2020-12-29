package com.classmanagement.modules.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final AccountValidator accountValidator;
    private final ObjectMapper objectMapper;

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

    @ResponseBody
    @GetMapping("/api/token")
    public String getToken(@RequestHeader("Authorization") String token) throws JsonProcessingException {
        System.out.println(token);

        String jwtToken = token.substring(7);
        Jwt decodedToken = JwtHelper.decode(jwtToken);
        Map<String, String> claims = objectMapper.readValue(decodedToken.getClaims(), Map.class);
        String username = claims.get("user_name");

        return username;
    }

}
