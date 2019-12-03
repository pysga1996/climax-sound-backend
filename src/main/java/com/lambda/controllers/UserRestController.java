package com.lambda.controllers;

import com.lambda.configurations.events.OnRegistrationCompleteEvent;
import com.lambda.models.entities.Role;
import com.lambda.models.entities.User;
import com.lambda.models.forms.UserForm;
import com.lambda.models.utilities.*;
import com.lambda.repositories.RoleRepository;
import com.lambda.services.UserService;
import com.lambda.services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@CrossOrigin(origins = {"https://climax-sound.netlify.com", "http://localhost:4200"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class UserRestController {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Autowired
    private Environment environment;

//    @Autowired
//    private JwtTokenProvider tokenProvider;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarStorageService avatarStorageService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    FormConvertService formConvertService;

    @PreAuthorize("permitAll()")
    @GetMapping("/profile/{id}")
    public ResponseEntity<User> getCurrentUser(@PathVariable("id") Long id) {
        User currentUser = userDetailService.getCurrentUser();
        User user = userService.setInfo(id, currentUser);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile multipartFile) {
        try {
            User currentUser = userDetailService.getCurrentUser();
            if (multipartFile != null) {
                String fileDownloadUri = avatarStorageService.saveToFirebaseStorage(currentUser, multipartFile);
                currentUser.setAvatarUrl(fileDownloadUri);
            }
            userService.save(currentUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            String error = e.getMessage();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody User user) {
        User oldUser = userDetailService.getCurrentUser();
        if (user != null) {
            userService.setFieldsEdit(oldUser, user);
            userService.save(oldUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

//    @PostMapping("/avatar")
//    public ResponseEntity<String> uploadAvatar(@RequestPart("avatar") MultipartFile avatar, @RequestPart("id") String id) {
//        Optional<User> user = userService.findById(Long.parseLong(id));
//        if (user.isPresent()) {
//            String fileName = avatarStorageService.saveToFirebaseStorage(user.get(), avatar);
//                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                        .path("/api/avatar/")
//                        .path(fileName)
//                        .toUriString();
//                user.get().setAvatarUrl(fileDownloadUri);
//                userService.save(user.get());
//            return new ResponseEntity<>("User's avatar uploaded successfully", HttpStatus.OK);
//        } else return new ResponseEntity<>("Not found user with the given id in database!", HttpStatus.NOT_FOUND);
//    }

    @GetMapping("/avatar/{fileName:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable("fileName") String fileName, HttpServletRequest request) {
        return downloadService.generateUrl(fileName, request, avatarStorageService);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping(value = "/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserForm userForm, WebRequest request) {
        try {
            User user = formConvertService.convertToUser(userForm, true);
//            if (user == null) {
//                throw new Exception();
//            }
            Role role = roleRepository.findByAuthority(DEFAULT_ROLE);
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            userService.save(user);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            for (StackTraceElement line: e.getStackTrace()) {
                stringBuilder.append(line).append("\n");
            }
            return new ResponseEntity<>(stringBuilder.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/registration-confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        StringBuilder uri = new StringBuilder(Objects.requireNonNull(environment.getProperty("FRONTEND_HOST"))).append("/complete-registration");
        try {
            userService.getVerificationToken(token);
            uri.append("?status=0");
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (InvalidTokenException ex) {
            uri.append("?status=").append(ex.getMessage());
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception ex) {
            uri.append("?status=").append(ex.getMessage());
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(HttpStatus.MOVED_PERMANENTLY);
        }
    }

//    @PreAuthorize("isAnonymous()")
//    @PostMapping(value = "/login")
//    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
//        // Xác thực từ username và password.
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsername(),
//                        loginRequest.getPassword()
//                )
//        );
//        // Nếu không xảy ra exception tức là thông tin hợp lệ
//        // Set thông tin authentication vào Security Context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        // Trả về jwt cho người dùng.
//        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
//        Optional<User> currentUser = userService.findByUsername(authentication.getName());
//        LoginResponse loginResponse = currentUser.map(user -> new LoginResponse(jwt, user.getId())).orElseGet(() -> new LoginResponse(jwt, null));
//        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
//    }

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
            boolean value = arrset1.equals(arrset2);
            return new RandomStuff(Boolean.toString(value));
//        return new RandomStuff("JWT Hợp lệ mới có thể thấy được message này");
        }

        @GetMapping(value = "/search", params = "name")
        public ResponseEntity<SearchResponse> search(@RequestParam("name") String name){
            try {
                SearchResponse searchResponse = userService.search(name);
                return new ResponseEntity<>(searchResponse, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        @PreAuthorize("hasRole('ADMIN')")
        @DeleteMapping(value = "/delete-user", params = "id")
        public ResponseEntity<Void> deleteUser(@RequestParam("id")Long id) {
            try {
                userService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
