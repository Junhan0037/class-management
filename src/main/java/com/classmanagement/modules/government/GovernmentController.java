package com.classmanagement.modules.government;

import com.classmanagement.infra.common.ErrorsResource;
import com.classmanagement.modules.account.Account;
import com.classmanagement.modules.account.AccountService;
import com.classmanagement.modules.account.Job;
import com.classmanagement.modules.account.Role;
import com.classmanagement.modules.classroom.Classroom;
import com.classmanagement.modules.classroom.ClassroomController;
import com.classmanagement.modules.classroom.ClassroomService;
import com.classmanagement.modules.token.TokenEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/government", produces = MediaTypes.HAL_JSON_VALUE)
public class GovernmentController {

    private final AccountService accountService;

    @GetMapping("/money") // 자신이 속한 학급 자산 통계
    public ResponseEntity createClassroom(@TokenEmail String currentUser) {
        Account user = accountService.findAccount(currentUser).orElseThrow(() -> new UsernameNotFoundException(currentUser));
        if (user.getJob() != Job.GOVERNMENT && user.getRole() != Role.TEACHER) {
            return ResponseEntity.badRequest().body("해당 사용자의 권한으로 접근할 수 없습니다.");
        }
        Classroom classroom = user.getClassroom(); // 학생이 속한 학급

        List<Account> members = classroom.getMembers();
        float total = 0;
        int size = members.size() - 1;
        for (Account member : members) {
            if (member == user) {
                continue;
            }
            total += member.getMoney();
        }
        GovernmentDto governmentDto = new GovernmentDto();
        governmentDto.setSum(total);
        governmentDto.setAverage(total / size);

        GovernmentResource governmentResource = new GovernmentResource(governmentDto);
        governmentResource.add(linkTo(GovernmentController.class).withRel("statistics-classroom"));
        governmentResource.add(new Link("/docs/government.html#resources-classroom-statistics").withRel("profile"));

        WebMvcLinkBuilder selfLinkBuilder = linkTo(GovernmentController.class).slash(classroom.getId());
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(governmentResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
