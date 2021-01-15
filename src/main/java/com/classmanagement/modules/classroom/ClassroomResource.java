package com.classmanagement.modules.classroom;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ClassroomResource extends EntityModel<Classroom> {

    public ClassroomResource(Classroom classroom, Link... links) {
        super(classroom, links);
        add(linkTo(ClassroomController.class).slash(classroom.getId()).withSelfRel());
    }

}
