package com.classmanagement.modules.classroom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class ClassroomDto {

    @NotEmpty
    private String name;

}
