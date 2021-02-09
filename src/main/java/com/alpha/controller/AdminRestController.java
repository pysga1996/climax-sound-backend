package com.alpha.controller;

import com.alpha.constant.CrossOriginConfig;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {CrossOriginConfig.Origins.ALPHA_SOUND, CrossOriginConfig.Origins.LOCAL_HOST},
        allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping(params = "action=list")
//    public ResponseEntity<Page<UserDTO>> getUserList(Pageable pageable) {
//        Page<UserDTO> userList = userService.findAll(pageable);
//        return new ResponseEntity<>(userList, HttpStatus.OK);
//    }
//
//    @GetMapping(params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UserDTO> getUserById(@RequestParam Long id) {
//        Optional<UserDTO> user = userService.findById(id);
//        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
//    }
//
//    @DeleteMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
//        userService.deleteById(id);
//        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
//    }
//
//    @GetMapping("/user-list")
//    public ResponseEntity<Page<UserDTO>> userList(Pageable pageable) {
//        Page<UserDTO> userList = userService.findAll(pageable);
//        if (userList.getTotalElements() == 0) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else return new ResponseEntity<>(userList, HttpStatus.OK);
//    }
}