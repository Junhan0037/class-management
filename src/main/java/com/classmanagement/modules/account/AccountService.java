package com.classmanagement.modules.account;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new AccountAdapter(account);
    }

    public Account saveAccount(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Page<Account> findAccount(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Optional<Account> findAccount (String email) {
        return accountRepository.findByEmail(email);
    }

    public Account updateAccount(AccountUpdateDto accountUpdateDto, Account existingAccount) {
        modelMapper.map(accountUpdateDto, existingAccount);
        return accountRepository.save(existingAccount);
    }

//    public List<Account> showAccount() {
//        List<Account> accounts = accountRepository.findAll();
//        return accounts;
//    }

}
