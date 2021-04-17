package com.alpha.util.helper;

import com.alpha.model.entity.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class UserInfoJsonStringifier {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static UserInfo stringify(Map<String, Object> userShortInfo) {
        String userInfoStr;
        String username = (String) userShortInfo.get("username");
        try {
            userInfoStr = mapper.writeValueAsString(userShortInfo);
        } catch (JsonProcessingException ex) {
            userInfoStr = "{}";
        }
        return new UserInfo(username, userInfoStr);
    }
}
