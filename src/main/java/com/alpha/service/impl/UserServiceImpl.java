package com.alpha.service.impl;

import com.alpha.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public OAuth2AuthenticatedPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal) {
            return (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public Map<String, Object> getCurrentUserShortInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal) {
            OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal =
                    (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
            Map<String, Object> shortInfo = new HashMap<>();
            shortInfo.put("username", oAuth2AuthenticatedPrincipal.getName());
            shortInfo.put("first_name", oAuth2AuthenticatedPrincipal.getAttribute("first_name"));
            shortInfo.put("last_name", oAuth2AuthenticatedPrincipal.getAttribute("last_name"));
            shortInfo.put("gender", oAuth2AuthenticatedPrincipal.getAttribute("gender"));
            shortInfo.put("date_of_birth", oAuth2AuthenticatedPrincipal.getAttribute("date_of_birth"));
            shortInfo.put("avatar_url", oAuth2AuthenticatedPrincipal.getAttribute("avatar_url"));
            return shortInfo;
        }
        return null;
    }

}
