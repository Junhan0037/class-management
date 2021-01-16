package com.classmanagement.modules.classroom;

import com.classmanagement.infra.common.ErrorsResource;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Role;
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
        Classroom newClassroom = classroomService.saveClassroom(classroom);

        ClassroomResource classroomResource = new ClassroomResource(newClassroom);
        classroomResource.add(linkTo(ClassroomController.class).withRel("create-classroom"));
        classroomResource.add(new Link("/docs/classroom.html#resources-classroom-create").withRel("profile"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ClassroomController.class).slash(newClassroom.getId());
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(classroomResource);
    }

    @GetMapping // 선생님이 관리중인 학급 리스트 조회
    public ResponseEntity queryClassrooms(Pageable pageable, PagedResourcesAssembler<Classroom> assembler, @TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() == Role.STUDENT) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Page<Classroom> classroomByAccount = classroomService.findClassroomByAccount(user, pageable);
        var pagedResources = assembler.toModel(classroomByAccount, e -> new ClassroomResource(e));
        pagedResources.add(new Link("/docs/classroom.html#resources-classrooms-list").withRel("profile"));
        pagedResources.add(linkTo(ClassroomController.class).withRel("query-classrooms"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ClassroomController.class);
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(pagedResources);
    }

    @PostMapping("/join") // 해당 학급 참여
    public ResponseEntity joinClassroom(@RequestParam Long id, @RequestParam String name, @TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Optional<Classroom> optionalClassroom = classroomService.findClassroom(id, name);
        if (optionalClassroom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 id, name의 학급이 존재하지 않습니다.");
        }
        Classroom classroom = optionalClassroom.get();

        classroom.addMembers(user);
        Classroom saveClassroom = classroomService.saveClassroom(classroom);

        ClassroomResource classroomResource = new ClassroomResource(saveClassroom);
        classroomResource.add(linkTo(ClassroomController.class).withRel("join-classroom"));
        classroomResource.add(new Link("/docs/classroom.html#resources-classroom-join").withRel("profile"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ClassroomController.class).slash(saveClassroom.getId());
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(classroomResource);
    }

    @PostMapping("/cancel") // 학생이 해당 학급 나가기
    public ResponseEntity cancelClassroom(@RequestParam Long id, @RequestParam String name, @TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getRole() != Role.STUDENT) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }

        Optional<Classroom> optionalClassroom = classroomService.findClassroom(id, name);
        if (optionalClassroom.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 id, name의 학급이 존재하지 않습니다.");
        }
        Classroom classroom = optionalClassroom.get();

        if (!classroom.getMembers().contains(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 학급에 학생이 존재하지 않습니다.");
        }

        classroom.cancelMember(user);
        Classroom saveClassroom = classroomService.saveClassroom(classroom);

        ClassroomResource classroomResource = new ClassroomResource(saveClassroom);
        classroomResource.add(linkTo(ClassroomController.class).withRel("cancel-classroom"));
        classroomResource.add(new Link("/docs/classroom.html#resources-classroom-cancel").withRel("profile"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ClassroomController.class).slash(saveClassroom.getId());
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(classroomResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
