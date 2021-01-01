package com.classmanagement.modules.account;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class AccountResource extends EntityModel<Account> {

    public AccountResource(Account account, Link... links) {
        super(account, links);
        add(linkTo(AccountController.class).slash(account.getId()).withSelfRel());
    }

}
