package com.lambda.model.util;

import com.lambda.model.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class LoginResponse {

    private String username;
    private String accessToken;
    private String tokenType = "Bearer";
    private Collection<Role> roles;
    private Collection<? extends GrantedAuthority> authorities;

    public LoginResponse() {
    }

    public LoginResponse(String username, Collection<Role> roles, Collection<? extends GrantedAuthority> authorities, String accessToken) {
        this.username = username;
        this.roles = roles;
        this.authorities = authorities;
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
