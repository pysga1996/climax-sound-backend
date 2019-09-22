package com.lambda.controller;

import com.lambda.model.User;
import com.lambda.service.UserService;
import com.lambda.service.impl.UserDetailServiceImpl;
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
    private UserService userService;

    @GetMapping(value = "", params = "action=list")
    public ResponseEntity<Page<User>> getUserList(Pageable pageable) {
        Page<User> userList = userService.findAll(pageable);
        return new ResponseEntity<Page<User>>(userList, HttpStatus.OK);
    }

    @GetMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getUserById(@RequestParam Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<Object>(user, HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Not Found User", HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<String>("Deleted!", HttpStatus.OK);
    }
}