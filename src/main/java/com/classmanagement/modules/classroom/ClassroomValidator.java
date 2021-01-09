package com.classmanagement.modules.classroom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class ClassroomValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(ClassroomDto.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        ClassroomDto classroomDto = (ClassroomDto) object;
    }

}
