package com.lambda.controller;

import com.lambda.model.entity.Role;
import com.lambda.model.entity.User;
import com.lambda.model.util.CustomUserDetails;
import com.lambda.model.util.LoginRequest;
import com.lambda.model.util.LoginResponse;
import com.lambda.model.util.RandomStuff;
import com.lambda.repository.RoleRepository;
import com.lambda.service.UserService;
import com.lambda.service.impl.JwtTokenProvider;
import com.lambda.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class UserRestController {
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
    public ResponseEntity<User> getCurrentUser() {
        User user = userDetailService.getCurrentUser();
        if (user != null) {
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@Valid @RequestBody User user) {
        User checkedUser = userService.findByUsername(user.getUsername());
        if (checkedUser != null) {
            return new ResponseEntity<String>("Username existed in database!", HttpStatus.BAD_REQUEST);
        }
//        if (bindingResult.hasFieldErrors()) {
//            List<FieldError> errors = bindingResult.getFieldErrors();
//            StringBuilder body = new StringBuilder();
//            for (FieldError error : errors ) {
//                body.append(error.getDefaultMessage());
//                body.append("\n");
//            }
//            return new ResponseEntity<String>(body.toString(), HttpStatus.BAD_REQUEST);
//        }
        String username = user.getUsername();
        String password = user.getPassword();
        Role role = roleRepository.findByName(DEFAULT_ROLE);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User newUser = new User(username, password, roles);
//        user.setRoles(roles);
//        user.setPassword(user.getPassword());
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Object> result = userService.save(newUser);
        return new ResponseEntity<String>((String) result.get(0), (HttpStatus) result.get(1));
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
        return new ResponseEntity<LoginResponse>(new LoginResponse(jwt), HttpStatus.OK);
    }

    @GetMapping("/random")
    public RandomStuff randomStuff(){
        // Creating object of Set
        Set<String> arrset1 = new HashSet<String>();

        // Populating arrset1
        arrset1.add("A");
        arrset1.add("B");
        arrset1.add("C");
        arrset1.add("D");
        arrset1.add("E");

        // print arrset1
        System.out.println("First Set: "
                + arrset1);

        // Creating another object of Set
        Set<String> arrset2 = new HashSet<String>();

        // Populating arrset2
        arrset2.add("C");
        arrset2.add("D");
        arrset2.add("A");
        arrset2.add("B");
        arrset2.add("E");

        // print arrset2
        System.out.println("Second Set: "
                + arrset2);
        Boolean value = arrset1.equals(arrset2);
        return new RandomStuff(value.toString());
//        return new RandomStuff("JWT Hợp lệ mới có thể thấy được message này");
    }
}
