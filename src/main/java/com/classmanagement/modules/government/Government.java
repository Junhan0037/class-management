package com.classmanagement.modules.government;

import com.classmanagement.modules.main.BaseTimeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor @NoArgsConstructor
public class Government extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

}
