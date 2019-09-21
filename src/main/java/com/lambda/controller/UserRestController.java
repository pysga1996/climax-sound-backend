package com.lambda.controller;

import com.lambda.model.User;
import com.lambda.service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
public class UserRestController {
    @Autowired
    private UserDetailServiceImpl userDetailService;

    @GetMapping(value = "", params = "action=list")
    public ResponseEntity<Page<User>> getUserList(Pageable pageable) {
        Page<User> userList = userDetailService.findAll(pageable);
        return new ResponseEntity<Page<User>>(userList, HttpStatus.OK);
    }

    @GetMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Optional<User> user = userDetailService.findUserById(id);
        if (user != null) {
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Not Found User", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userDetailService.deleteUser(id);
        return new ResponseEntity<String>("Deleted!", HttpStatus.OK);
    }
}