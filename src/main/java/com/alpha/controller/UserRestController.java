package com.alpha.controller;

import com.alpha.model.dto.UserDTO;
import com.alpha.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api")
public class UserRestController {
    //    private Environment environment;
//
//    @Autowired
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//    //    @Autowired
////    private JwtTokenProvider tokenProvider;
//
//    private ApplicationEventPublisher eventPublisher;
//
//    @Autowired
//    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
//        this.eventPublisher = eventPublisher;
//    }
//
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    //
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Autowired
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }
//
//    private AvatarStorageService avatarStorageService;
//
//    @Autowired
//    public void setAvatarStorageService(AvatarStorageService avatarStorageService) {
//        this.avatarStorageService = avatarStorageService;
//    }
//
//    private DownloadService downloadService;
//
//    @Autowired
//    public void setDownloadService(DownloadService downloadService) {
//        this.downloadService = downloadService;
//    }
//
    @PreAuthorize("#oauth2.user")
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUser() {
        try {
            UserDTO currentUser = this.userService.getCurrentUser();
            if (currentUser != null) {
                return new ResponseEntity<>(currentUser, HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//
//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/upload-avatar")
//    public ResponseEntity<String> uploadAvatar(@RequestParam("avatar") MultipartFile multipartFile) {
//        try {
//            UserDTO currentUser = userService.getCurrentUser();
//            if (multipartFile != null) {
//                String fileDownloadUri = avatarStorageService.saveToFirebaseStorage(currentUser, multipartFile);
//                currentUser.setAvatarUrl(fileDownloadUri);
//            }
//            userService.save(currentUser, false);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            String error = e.getMessage();
//            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("isAuthenticated()")
//    @PutMapping("/profile")
//    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UserDTO user) {
//        try {
//            UserDTO oldUser = userService.getCurrentUser();
//            userService.setFieldsEdit(oldUser, user);
//            userService.save(oldUser, false);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (NullPointerException ex) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        } catch (Exception ex) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
////    @PostMapping("/avatar")
////    public ResponseEntity<String> uploadAvatar(@RequestPart("avatar") MultipartFile avatar, @RequestPart("id") String id) {
////        Optional<User> user = userService.findById(Long.parseLong(id));
////        if (user.isPresent()) {
////            String fileName = avatarStorageService.saveToFirebaseStorage(user.get(), avatar);
////                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
////                        .path("/api/avatar/")
////                        .path(fileName)
////                        .toUriString();
////                user.get().setAvatarUrl(fileDownloadUri);
////                userService.save(user.get());
////            return new ResponseEntity<>("User's avatar uploaded successfully", HttpStatus.OK);
////        } else return new ResponseEntity<>("Not found user with the given id in database!", HttpStatus.NOT_FOUND);
////    }
//
////    @GetMapping("/avatar/{fileName:.+}")
////    public ResponseEntity<Resource> getAvatar(@PathVariable("fileName") String fileName, HttpServletRequest request) {
////        Path path = Paths.get("");
////        if (storageService instanceof AudioStorageService) {
////            path = ((AudioStorageService) storageService).audioStorageLocation;
////        } else if (storageService instanceof CoverStorageService) {
////            path = ((CoverStorageService) storageService).coverStorageLocation;
////        } else if (storageService instanceof AvatarStorageService) {
////            path = ((AvatarStorageService) storageService).avatarStorageLocation;
////        }
//        // Load file as Resource
//        Resource resource = storageService.loadFileAsResource(path, fileName);
//        // Try to determine file's content type
//        String contentType = null;
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            logger.info("Could not determine file type.");
//        }
//        // Fallback to the default content type if type could not be determined
//        if (contentType == null) {
//            contentType = "application/octet-stream";
//        }
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
////    }
//
//    @PreAuthorize("isAnonymous()")
//    @PostMapping(value = "/register")
//    public ResponseEntity<String> createUser(@Valid @RequestBody UserDTO user, WebRequest request) {
//        try {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            UserDTO savedUser = userService.save(user, true);
//            if (savedUser == null) {
//                return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
//            }
//            String appUrl = request.getContextPath();
//            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
//            return new ResponseEntity<>( HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("permitAll()")
//    @GetMapping("/registration-confirm")
//    public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
//        HttpHeaders headers = new HttpHeaders();
//        StringBuilder uri = new StringBuilder(Objects.requireNonNull(environment.getProperty("FRONTEND_HOST"))).append("/complete-registration");
//        try {
//            userService.getVerificationToken(token);
//            uri.append("?status=0");
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        } catch (InvalidTokenException ex) {
//            uri.append("?status=").append(ex.getMessage());
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        } catch (Exception ex) {
//            uri.append("?status=3");
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        }
//    }
//
//    @PreAuthorize("permitAll()")
//    @PostMapping("/reset-password")
//    public ResponseEntity<Void> resetPassword(@RequestBody GetResetPasswordTokenForm getResetPasswordTokenForm, WebRequest request) {
//        try {
//            String userEmail = getResetPasswordTokenForm.getEmail();
//            UserDTO user = userService.findByEmail(userEmail);
//            if (user == null) {
//                throw new UserNotFoundException();
//            }
//            String appUrl = request.getContextPath();
//            eventPublisher.publishEvent(new OnResetPasswordEvent(user, request.getLocale(), appUrl));
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PreAuthorize("permitAll()")
//    @GetMapping(value = "/reset-password", params = {"id", "token"})
//    public ResponseEntity showChangePasswordPage(@RequestParam("id") long id, @RequestParam("token") String token) {
//        HttpHeaders headers = new HttpHeaders();
//        StringBuilder uri = new StringBuilder(Objects.requireNonNull(environment.getProperty("FRONTEND_HOST"))).append("/reset-password-submission");
//        try {
//            userService.validatePasswordResetToken(id, token);
//            uri.append("?status=0").append("&id=").append(id).append("&token=").append(token);
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        } catch (InvalidTokenException ex) {
//            uri.append("?status=").append(ex.getMessage());
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        } catch (Exception ex) {
//            uri.append("?status=3");
//            headers.setLocation(URI.create(uri.toString()));
//            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
//        }
//    }
//
//    @PreAuthorize("permitAll()")
//    @PutMapping(value = "/reset-password", params = {"id", "token"})
//    public ResponseEntity<String> savePassword(@Valid @RequestBody PasswordDto passwordDto, @RequestParam("id") Long id, @RequestParam("token") String token) {
//        try {
//            userService.validatePasswordResetToken(id, token);
//            UserDTO user = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            userService.changeUserPassword(user, passwordDto.getNewPassword());
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (InvalidTokenException ex) {
//            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception ex) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
////    @PreAuthorize("isAnonymous()")
////    @PostMapping(value = "/login")
////    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
////        // Xác thực từ username và password.
////        Authentication authentication = authenticationManager.authenticate(
////                new UsernamePasswordAuthenticationToken(
////                        loginRequest.getUsername(),
////                        loginRequest.getPassword()
////                )
////        );
////        // Nếu không xảy ra exception tức là thông tin hợp lệ
////        // Set thông tin authentication vào Security Context
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////        // Trả về jwt cho người dùng.
////        String jwt = tokenProvider.generateToken((CustomUserDetails) authentication.getPrincipal());
////        Optional<User> currentUser = userService.findByUsername(authentication.getName());
////        LoginResponse loginResponse = currentUser.map(user -> new LoginResponse(jwt, user.getId())).orElseGet(() -> new LoginResponse(jwt, null));
////        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
////    }
//
//        @GetMapping(value = "/search", params = "name")
//        public ResponseEntity<SearchResponse> search(@RequestParam("name") String name){
//            try {
//                SearchResponse searchResponse = userService.search(name);
//                return new ResponseEntity<>(searchResponse, HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        @PreAuthorize("hasRole('ADMIN')")
//        @DeleteMapping(value = "/delete-user", params = "id")
//        public ResponseEntity<Void> deleteUser(@RequestParam("id")Long id) {
//            try {
//                userService.deleteById(id);
//                return new ResponseEntity<>(HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        @PreAuthorize("isAuthenticated()")
//        @PostMapping("/setting")
//        public ResponseEntity<Void> changeSetting(@RequestBody SettingDTO setting) {
//            try {
//                this.userService.changeSetting(setting);
//                return new ResponseEntity<>(HttpStatus.OK);
//            } catch (Exception ex) {
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
//        @GetMapping("/test")
//        public ResponseEntity<String> test() {
//            try {
//                return new ResponseEntity<>("Test",HttpStatus.OK);
//            } catch (Exception e) {
//                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//
}
