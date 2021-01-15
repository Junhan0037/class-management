package com.classmanagement.modules.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired AccountService accountService;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        //given
        String username = "junhan@email.com";
        String password = "123";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .name("junhan")
                .role(Role.STUDENT)
                .build();
        accountService.saveAccount(account);

        //when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        //then
        assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
    }

    @Test
    public void findByUsernameFail() { // 예외테스트
        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername("random@email.com"));
    }

}