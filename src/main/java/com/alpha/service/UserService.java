package com.alpha.service;

import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;

public interface UserService {

    String getCurrentUsername();

    boolean isAnonymous();

    boolean isAuthenticated();

    boolean hasAuthority(String authority);

    UserInfoDTO getCurrentProfile();

    UserInfoDTO getUserInfo(String username);

    void applySetting(Map<String, Object> setting) throws JsonProcessingException;

}
