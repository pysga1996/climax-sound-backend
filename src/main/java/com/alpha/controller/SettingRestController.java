package com.alpha.controller;

import com.alpha.model.dto.UserInfoDTO;
import com.alpha.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thanhvt
 * @created 05/06/2021 - 11:27 CH
 * @project vengeance
 * @since 1.0
 **/
@RestController
@RequestMapping("/api/setting")
public class SettingRestController {

    private final UserService userService;

    @Autowired
    public SettingRestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getSetting() {
        UserInfoDTO userInfoDTO = this.userService.getCurrentUserInfoDTO();
        return ResponseEntity.ok(userInfoDTO.getSetting());
    }

    @PatchMapping
    public ResponseEntity<Void> applySetting(@RequestBody Map<String, Object> setting)
        throws JsonProcessingException {
        this.userService.applySetting(setting);
        return ResponseEntity.ok().build();
    }

}
