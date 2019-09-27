package com.lambda.model.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Collection<? extends GrantedAuthority> roles;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
