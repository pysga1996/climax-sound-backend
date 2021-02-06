package com.alpha.config.custom;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {

    private String authService;

    public String getAuthService() {
        return authService;
    }

    public void setAuthService(String authService) {
        this.authService = authService;
    }
}
