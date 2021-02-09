package com.alpha.util.helper;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.dto.UserProfileDTO;
import com.alpha.model.entity.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public final class UserInfoJsonStringifier {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static UserInfoDTO stringify(UserDTO userDTO) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", userDTO.getUsername());
        if (userDTO.getUserProfile() != null) {
            UserProfileDTO userProfileDTO = userDTO.getUserProfile();
            userInfo.put("firstName", userProfileDTO.getFirstName());
            userInfo.put("lastName", userProfileDTO.getFirstName());
            userInfo.put("avatarUrl", userProfileDTO.getFirstName());
        }
        String userInfoStr;
        try {
            userInfoStr = mapper.writeValueAsString(userInfo);
        } catch (JsonProcessingException ex) {
            userInfoStr = "{}";
        }
        return new UserInfoDTO(userDTO.getId(), userInfoStr);
    }
}
