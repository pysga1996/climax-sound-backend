package com.alpha.service;

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;

import java.util.Map;

public interface UserService {

    OAuth2AuthenticatedPrincipal getCurrentUser();

    Map<String, Object> getCurrentUserShortInfo();

}
