package com.lambda.controller;

import com.lambda.model.*;
import com.lambda.repository.RoleRepository;
import com.lambda.service.UserService;
import com.lambda.service.impl.JwtTokenProvider;
import com.lambda.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class SecurityRestController {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImpl userDetailService;
    
    @Autowired
    private UserService userService;

    @GetMapping(value = "/profile")
    public ResponseEntity<Object> getCurrentUser() {
        User user = userDetailService.getCurrentUser();
        if (user != null) {
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Not Found User", HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@RequestBody User user, Pageable pageable) {
        User checkedUser = userService.findByUsername(user.getUsername());
        if (checkedUser == null) {
            return new ResponseEntity<>("User Existed!", HttpStatus.BAD_REQUEST);
        }
        Role role = roleRepository.findByName(DEFAULT_ROLE);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return new ResponseEntity<>("Created!", HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Xác thực từ username và password.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // Nếu không xảy ra exception tức là thông tin hợp lệ
        // Set thông tin authentication vào Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Trả về jwt cho người dùng.
        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
        return new ResponseEntity<>(new LoginResponse(jwt), HttpStatus.OK);
    }

    @GetMapping("/random")
    public RandomStuff randomStuff(){
        return new RandomStuff("JWT Hợp lệ mới có thể thấy được message này");
    }
}
