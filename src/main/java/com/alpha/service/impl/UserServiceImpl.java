package com.alpha.service.impl;

import com.alpha.model.dto.SettingDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.dto.UserProfileDTO;
import com.alpha.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal) {
            OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(principal.getAttribute("id"));
            userDTO.setUsername(principal.getName());
            userDTO.setAuthorities(new HashSet<>(principal.getAuthorities()));
            UserProfileDTO userProfile = this.objectMapper
                    .convertValue(principal.getAttribute("profile"), UserProfileDTO.class);
            userDTO.setUserProfile(userProfile);
            SettingDTO setting = this.objectMapper
                    .convertValue(principal.getAttribute("setting"), SettingDTO.class);
            userDTO.setSetting(setting);
            return userDTO;
        }
        return null;
    }

}
