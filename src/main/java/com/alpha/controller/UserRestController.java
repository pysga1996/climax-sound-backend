package com.alpha.controller;

import com.alpha.model.dto.UserInfoDTO;
import com.alpha.service.UserService;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/api")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoDTO> getProfile(@RequestParam("username") String username) {
        UserInfoDTO userInfoDTO = this.userService.getUserInfo(username);
        return new ResponseEntity<>(userInfoDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        Map<String, Object> currentUserProfile = this.userService.getCurrentUserProfile();
        return new ResponseEntity<>(currentUserProfile, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/profile")
    public ResponseEntity<Map<String, ?>> updateProfile(@RequestPart("profile") String userProfile,
        @RequestPart("avatar") MultipartFile avatar) {
        Map<String, Object> newProfile = this.userService.updateUserProfile(userProfile, avatar);
        return new ResponseEntity<>(newProfile, HttpStatus.OK);
    }

    @GetMapping("/is-admin")
    public ResponseEntity<Boolean> checkIsAdmin() {
        boolean isAdmin = this.userService.isAdmin();
        return new ResponseEntity<>(isAdmin, HttpStatus.OK);
    }

    @GetMapping("/is-anonymous")
    public ResponseEntity<Boolean> checkIsAnonymous() {
        boolean isAnonymous = this.userService.isAnonymous();
        return new ResponseEntity<>(isAnonymous, HttpStatus.OK);
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> checkIsAuthenticated() {
        boolean isAuthenticated = this.userService.isAuthenticated();
        return new ResponseEntity<>(isAuthenticated, HttpStatus.OK);
    }

    @GetMapping("/has-access")
    public ResponseEntity<Boolean> checkIsAdmin(@RequestParam("menu") String menu) {
        boolean isAdmin = this.userService.hasAuthority(menu);
        return new ResponseEntity<>(isAdmin, HttpStatus.OK);
    }

}
