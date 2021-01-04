package com.classmanagement.modules.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, String> {



}
