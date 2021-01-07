package com.classmanagement.modules.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, String> {

    Optional<OauthClientDetails> findByClientId(Long id);

}
