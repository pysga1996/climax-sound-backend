package com.alpha.service.impl;

import com.alpha.constant.CommonConstants;
import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.UserInfoRepository;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserInfoRepository userInfoRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final StorageService storageService;

    @Autowired
    public UserServiceImpl(ObjectMapper objectMapper,
        UserInfoMapper userInfoMapper,
        UserInfoRepository userInfoRepository,
        ResourceInfoRepository resourceInfoRepository, StorageService storageService) {
        this.objectMapper = objectMapper;
        this.userInfoMapper = userInfoMapper;
        this.userInfoRepository = userInfoRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.storageService = storageService;
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
    public UserInfoDTO getCurrentUserInfo() {
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
                .profile("{\"username\": \"" + username + "\"}")
                .setting("{\"darkMode\": false}")
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
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentUserProfile() {
        String username = this.getCurrentUsername();
        Optional<UserInfo> userInfoOptional = this.userInfoRepository.findByUsername(username);
        if (userInfoOptional.isPresent()) {
            Map<String, Object> profileJson = this.objectMapper
                .readValue(userInfoOptional.get().getProfile(), Map.class);
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByUsernameAndStorageTypeAndMediaRefAndStatus(username,
                    this.storageService.getStorageType(), MediaRef.USER_AVATAR, Status.ACTIVE);
            optionalResourceInfo.ifPresent(resourceInfo -> profileJson
                .put("avatar_url", this.storageService.getFullUrl(resourceInfo)));
            return profileJson;
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    @Transactional
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public Map<String, Object> updateUserProfile(String profile,
        MultipartFile avatar) {
        String username = this.getCurrentUsername();
        Optional<UserInfo> userInfoOptional = this.userInfoRepository.findByUsername(username);
        if (userInfoOptional.isPresent()) {
            Map<String, Object> profileJson = this.objectMapper.readValue(profile, Map.class);
            userInfoOptional.get().setProfile(profile);
            ResourceInfo oldResourceInfo = this.resourceInfoRepository
                .findByUsernameAndStorageTypeAndMediaRefAndStatus(username,
                    this.storageService.getStorageType(), MediaRef.USER_AVATAR, Status.ACTIVE)
                .orElse(null);
            ResourceInfo newResourceInfo = this.storageService
                .upload(avatar, userInfoOptional.get(), oldResourceInfo);
            profileJson.put("avatar_url", this.storageService.getFullUrl(newResourceInfo));
            return profileJson;
        } else {
            throw new EntityNotFoundException("User not found");
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
