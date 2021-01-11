package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    public Page<Classroom> findClassroomByAccount(Account account, Pageable pageable) {
        return classroomRepository.findByTeacher(account, pageable);
    }

}
