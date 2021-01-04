package com.classmanagement.modules.account;

import com.classmanagement.infra.common.AppProperties;
import com.classmanagement.infra.common.ErrorsResource;
import com.classmanagement.modules.token.TokenEmail;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/accounts", produces = MediaTypes.HAL_JSON_VALUE)
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final AccountValidator accountValidator;
    private final AppProperties appProperties;

//    @InitBinder("accountDto")
//    public void initBinder(WebDataBinder webDataBinder) {
//        webDataBinder.addValidators(accountValidator);
//    }

    @PostMapping // 회원 등록
    public ResponseEntity createAccount(@RequestBody @Valid AccountDto accountDto, Errors errors) {
        if (errors.hasErrors()) { // 입력값이 비어있는 경우
            return badRequest(errors);
        }

        accountValidator.validate(accountDto, errors);
        if (errors.hasErrors()) { // 입력값이 잘못된 경우
            return badRequest(errors);
        }

        Account account = modelMapper.map(accountDto, Account.class);
//        account.setAuthorizationID(appProperties.getClientId());
//        account.setAuthorizationPW(appProperties.getClientSecret());
        Account newAccount = accountService.saveAccount(account);

        AccountResource accountResource = new AccountResource(newAccount); // "/api/{id}"
        accountResource.add(linkTo(AccountController.class).withRel("create-account")); // "/api/accounts"
        accountResource.add(new Link("/docs/account.html#resources-accounts-create").withRel("profile")); // docs link

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(newAccount.getId()); // Location Header
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(accountResource);
    }

    @GetMapping // 모든 회원 조회 (TODO 선생님용 회원 목록 조회 개발 필요!)
    public ResponseEntity queryAccounts(Pageable pageable, PagedResourcesAssembler<Account> assembler, @TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Page<Account> page = accountService.findAccount(pageable);
        var pagedResources = assembler.toModel(page, e -> new AccountResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-accounts-list").withRel("profile"));
        pagedResources.add(linkTo(AccountController.class).withRel("query-accounts"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class); // Location Header
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(pagedResources);
    }

    @GetMapping("{email}") // 회원 단건 조회 (TODO 선생님용 회원 단건 조회 개발 필요!)
    public ResponseEntity getAccount(@PathVariable String email, @TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Optional<Account> optionalAccount = accountService.findAccount(email);
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 email의 회원 정보가 없습니다.");
        }

        Account findAccount = optionalAccount.get();
        AccountResource accountResource = new AccountResource(findAccount);
        accountResource.add(new Link("/docs/index.html#resources-accounts-get").withRel("profile"));
        accountResource.add(linkTo(AccountController.class).slash(findAccount.getEmail()).withRel("query-account"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(findAccount.getEmail()); // Location Header
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(accountResource);
    }

    @PutMapping("{email}") // 회원 수정 (TODO 현재는 이름만 수정 가능!)
    public ResponseEntity updateAccount(@RequestBody @Valid AccountUpdateDto accountUpdateDto, Errors errors, @PathVariable String email, @TokenEmail String currentUser) {
        if (errors.hasErrors()) { // 입력값이 비어있는 경우
            return badRequest(errors);
        }

        Optional<Account> optionalAccount = accountService.findAccount(email);
        if (optionalAccount.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 email의 회원 정보가 없습니다.");
        }

        Account existingAccount = optionalAccount.get();
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() != Role.ADMIN || user.getRole() != existingAccount.getRole()) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Account savedAccount = accountService.updateAccount(accountUpdateDto, existingAccount);

        AccountResource accountResource = new AccountResource(savedAccount);
        accountResource.add(new Link("/docs/index.html#resources-accounts-update").withRel("profile"));
        accountResource.add(linkTo(AccountController.class).slash(email).withRel("update-account"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(savedAccount.getEmail()); // Location Header
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(accountResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors)); // Errors Serializer
    }

}
