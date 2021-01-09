package com.classmanagement.modules.classroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

}
