package com.lambda.controllers;

import com.lambda.configurations.events.OnRegistrationCompleteEvent;
import com.lambda.configurations.events.OnResetPasswordEvent;
import com.lambda.exceptions.UserNotFoundException;
import com.lambda.models.entities.Role;
import com.lambda.models.entities.User;
import com.lambda.models.forms.GetResetPasswordTokenForm;
import com.lambda.models.forms.PasswordDto;
import com.lambda.models.forms.UserForm;
import com.lambda.models.utilities.*;
import com.lambda.repositories.RoleRepository;
import com.lambda.services.UserService;
import com.lambda.services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

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
        try {
            User currentUser = userDetailService.getCurrentUser();
            User user = userService.setInfo(id, currentUser);
            if (user != null) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody User user) {
        try {
            User oldUser = userDetailService.getCurrentUser();
            userService.setFieldsEdit(oldUser, user);
            userService.save(oldUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

//    @GetMapping("/avatar/{fileName:.+}")
//    public ResponseEntity<Resource> getAvatar(@PathVariable("fileName") String fileName, HttpServletRequest request) {
//        return downloadService.generateUrl(fileName, request, avatarStorageService);
//    }

    @PreAuthorize("isAnonymous()")
    @PostMapping(value = "/register")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserForm userForm, WebRequest request) {
        try {
            User user = formConvertService.convertToUser(userForm, true);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
            }
            Role role = roleRepository.findByAuthority(DEFAULT_ROLE);
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            userService.save(user);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception ex) {
            uri.append("?status=3");
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody GetResetPasswordTokenForm getResetPasswordTokenForm, WebRequest request) {
        try {
            String userEmail = getResetPasswordTokenForm.getEmail();
            User user = userService.findByEmail(userEmail);
            if (user == null) {
                throw new UserNotFoundException();
            }
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnResetPasswordEvent(user, request.getLocale(), appUrl));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/reset-password", params = {"id", "token"})
    public ResponseEntity showChangePasswordPage(@RequestParam("id") long id, @RequestParam("token") String token) {
        HttpHeaders headers = new HttpHeaders();
        StringBuilder uri = new StringBuilder(Objects.requireNonNull(environment.getProperty("FRONTEND_HOST"))).append("/reset-password-submission");
        try {
            userService.validatePasswordResetToken(id, token);
            uri.append("?status=0").append("&id=").append(id).append("&token=").append(token);
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (InvalidTokenException ex) {
            uri.append("?status=").append(ex.getMessage());
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception ex) {
            uri.append("?status=3");
            headers.setLocation(URI.create(uri.toString()));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @PreAuthorize("permitAll()")
    @PutMapping(value = "/reset-password", params = {"id", "token"})
    public ResponseEntity<String> savePassword(@Valid @RequestBody PasswordDto passwordDto, @RequestParam("id") Long id, @RequestParam("token") String token) {
        try {
            userService.validatePasswordResetToken(id, token);
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            userService.changeUserPassword(user, passwordDto.getNewPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidTokenException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
