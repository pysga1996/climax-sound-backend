package com.alpha.config.custom;

import com.alpha.model.dto.SettingDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.dto.UserProfileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Primary
@SuppressWarnings("deprecation")
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter
        implements UserAuthenticationConverter {

    private final ObjectMapper mapper;

    private final String OTHER_INFO = "other";

    private final String USER_PROFILE = "profile";

    private final String SETTING = "setting";

    private final String ID = "id";

    @Autowired
    public CustomUserAuthenticationConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        if (authentication.getPrincipal() instanceof UserDTO) {
            response.put(ID, ((UserDTO) authentication.getPrincipal()).getId());
            response.put(OTHER_INFO, ((UserDTO) authentication.getPrincipal()).getBasicInfo());
            response.put(USER_PROFILE, ((UserDTO) authentication.getPrincipal()).getUserProfile());
            response.put(SETTING, ((UserDTO) authentication.getPrincipal()).getSetting());
        }
        return response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            try {
                UserDTO userDTO = new UserDTO();
                userDTO.setUsername(USERNAME);
                userDTO.setAuthorities(new HashSet<>(this.getAuthorities(map)));
                if (map.containsKey(ID)) {
                    userDTO.setId(Long.valueOf((Integer) map.get(ID)));
                }
                if (map.containsKey(OTHER_INFO)) {
                    Map<String, Boolean> otherInfo = (Map<String, Boolean>) map.get(OTHER_INFO);
                    userDTO.setEnabled(otherInfo.get("enabled"));
                    userDTO.setAccountNonLocked(otherInfo.get("accountNonLocked"));
                    userDTO.setAccountNonExpired(otherInfo.get("accountNonExpired"));
                    userDTO.setCredentialsNonExpired(otherInfo.get("credentialsNonExpired"));
                }
                if (map.containsKey(USER_PROFILE)) {
                    UserProfileDTO profileDTO = this.mapper.convertValue(map.get(USER_PROFILE), UserProfileDTO.class);
                    userDTO.setUserProfile(profileDTO);
                }
                if (map.containsKey(SETTING)) {
                    SettingDTO settingDTO = this.mapper.convertValue(map.get(SETTING), SettingDTO.class);
                    if (settingDTO == null) {
                        settingDTO = new SettingDTO();
                    }
                    userDTO.setSetting(settingDTO);
                }
                return new UsernamePasswordAuthenticationToken(userDTO, "N/A", userDTO.getAuthorities());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
