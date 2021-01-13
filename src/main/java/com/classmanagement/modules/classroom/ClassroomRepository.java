package com.classmanagement.modules.classroom;

import com.classmanagement.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    Page<Classroom> findByTeacher(Account account, Pageable pageable);

    Optional<Classroom> findByIdAndName(Long id, String name);

}
