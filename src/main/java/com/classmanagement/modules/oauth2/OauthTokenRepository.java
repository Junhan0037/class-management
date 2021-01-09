package com.classmanagement.modules.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthTokenRepository extends JpaRepository<OauthToken, String> {
}
