package com.classmanagement.infra.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter
@Setter
public class AppProperties {

    @NotEmpty
    private String adminUsername;

    @NotEmpty
    private String adminPassword;

    @NotEmpty
    private String teacherUsername;

    @NotEmpty
    private String teacherPassword;

    @NotEmpty
    private String studentUsername;

    @NotEmpty
    private String studentPassword;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String clientSecret;

}
