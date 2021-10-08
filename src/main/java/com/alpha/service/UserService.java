package com.alpha.service;

import com.alpha.elastic.model.MediaEs;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    String getCurrentUsername();

    boolean isAdmin();

    boolean isAnonymous();

    boolean isAuthenticated();

    boolean hasAuthority(String authority);

    UserInfo getCurrentUserInfo();

    UserInfoDTO getCurrentUserInfoDTO();

    UserInfoDTO getUserInfo(String username);

    Map<String, Object> getCurrentUserProfile();

    Map<String, Object> updateUserProfile(String profile, MultipartFile avatar);

    void applySetting(Map<String, Object> setting) throws JsonProcessingException;

}
