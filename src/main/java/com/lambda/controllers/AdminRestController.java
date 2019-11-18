package com.lambda.controllers;

import com.lambda.models.entities.User;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = {"https://climax-sound.netlify.com", "http://localhost:4200"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    @Autowired
    private UserService userService;

    @GetMapping(params = "action=list")
    public ResponseEntity<Page<User>> getUserList(Pageable pageable) {
        Page<User> userList = userService.findAll(pageable);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping(params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUserById(@RequestParam Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @DeleteMapping(value = "", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

    @GetMapping("/user-list")
    public ResponseEntity<Page<User>> userList(Pageable pageable) {
        Page<User> userList = userService.findAll(pageable);
        if (userList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(userList, HttpStatus.OK);
    }
}