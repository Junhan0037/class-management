package com.classmanagement.modules.government;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class GovernmentResource extends EntityModel<GovernmentDto> {

    public GovernmentResource(GovernmentDto governmentDto, Link... links) {
        super(governmentDto, links);
        add(linkTo(GovernmentController.class).withSelfRel());
    }

}
