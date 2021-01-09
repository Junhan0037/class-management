package com.classmanagement.modules.classroom;

import com.classmanagement.infra.common.ErrorsResource;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountController;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
import com.classmanagement.modules.token.TokenEmail;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/classroom", produces = MediaTypes.HAL_JSON_VALUE)
public class ClassroomController {

    private final ClassroomValidator classroomValidator;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final ClassroomService classroomService;

    @PostMapping // 학급 등록
    public ResponseEntity createClassroom(@RequestBody @Valid ClassroomDto classroomDto, Errors errors, @TokenEmail String currentUser) {
        if (errors.hasErrors()) { // 입력값이 비어있는 경우
            return badRequest(errors);
        }

        classroomValidator.validate(classroomDto, errors);
        if (errors.hasErrors()) { // 입력값이 잘못된 경우
            return badRequest(errors);
        }

        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() == Role.STUDENT) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Classroom classroom = modelMapper.map(classroomDto, Classroom.class);
        classroom.setTeacher(user);
        classroom.addMembers(user);
        Classroom newClassroom = classroomService.saveClassroom(classroom);

        ClassroomResource classroomResource = new ClassroomResource(newClassroom);
        classroomResource.add(linkTo(ClassroomController.class).withRel("create-classroom"));
        classroomResource.add(new Link("/docs/classroom.html#resources-classroom-create").withRel("profile"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash(newClassroom.getId());
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(classroomResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors)); // Errors Serializer
    }

}
