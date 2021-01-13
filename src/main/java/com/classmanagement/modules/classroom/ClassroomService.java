package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public Optional<Classroom> findClassroom(Long id, String name) {
        return classroomRepository.findByIdAndName(id, name);
    }

}
