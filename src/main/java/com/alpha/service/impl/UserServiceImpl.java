package com.alpha.service.impl;

import com.alpha.constant.CommonConstants;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.UserInfoRepository;
import com.alpha.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserInfoMapper userInfoMapper;

    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserServiceImpl(ObjectMapper objectMapper,
        UserInfoMapper userInfoMapper,
        UserInfoRepository userInfoRepository) {
        this.objectMapper = objectMapper;
        this.userInfoMapper = userInfoMapper;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2AuthenticatedPrincipal) {
            username = ((OAuth2AuthenticatedPrincipal) principal)
                .getAttribute(CommonConstants.USERNAME);
        } else if (principal instanceof Jwt) {
            username = ((Jwt) principal).getClaim(CommonConstants.USERNAME);
        } else {
            username = authentication.getName();
        }
        if (username == null) {
            throw new AuthenticationException(
                "No user_name field found in jwt claims/opaque token introspected response") {
            };
        }
        return username;
    }

    @Override
    public boolean isAnonymous() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @Override
    public boolean isAuthenticated() {
        return !this.isAnonymous();
    }

    @Override
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(authority);
    }

    @Override
    @Transactional
    public UserInfoDTO getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;
        if (authentication instanceof JwtAuthenticationToken) {
            // oauth2
            Map<String, Object> tokenAttributes = ((JwtAuthenticationToken) authentication)
                .getTokenAttributes();
            boolean hasUsername = tokenAttributes.containsKey("user_name");
            if (!hasUsername) {
                throw new RuntimeException("Jwt does not contains user_name field!!!");
            }
            username = (String) tokenAttributes.get("user_name");
        } else {
            // opaque
            username = authentication.getName();
        }
        Optional<UserInfo> userInfoOptional = this.userInfoRepository.findByUsername(username);
        if (userInfoOptional.isPresent()) {
            return this.userInfoMapper.entityToDto(userInfoOptional.get());
        } else {
            UserInfo userInfo = UserInfo
                .builder()
                .username(username)
                .profile("{}")
                .setting("{}")
                .build();
            this.userInfoRepository.save(userInfo);
            return this.userInfoMapper.entityToDto(userInfo);
        }
    }

    @Override
    public UserInfoDTO getUserInfo(String username) {
        Optional<UserInfo> userInfoOptional = this.userInfoRepository.findByUsername(username);
        if (userInfoOptional.isPresent()) {
            return this.userInfoMapper.entityToDto(userInfoOptional.get());
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    @Transactional
    public void applySetting(Map<String, Object> setting) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String settingJson = this.objectMapper.writeValueAsString(setting);
        this.userInfoRepository.applySetting(username, settingJson);
    }
}
