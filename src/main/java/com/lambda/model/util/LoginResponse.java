package com.lambda.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lambda.model.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class LoginResponse {
    @JsonProperty
    private Long id;

    @JsonProperty
    private String username;

    @JsonProperty
    private String accessToken;

    @JsonProperty
    private String tokenType = "Bearer";

    @JsonProperty
    private Collection<Role> roles;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String username, Collection<Role> roles, String accessToken) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.accessToken = accessToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
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
