package com.lambda.service.impl;

import com.lambda.model.entity.User;
import com.lambda.repository.UserRepository;
import com.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> findByUsernameContaining(String username, Pageable pageable) {
        return userRepository.findByUsernameContaining(username, pageable);
    }

    @Override
    public Page<User> findByRoles_Name(String username, Pageable pageable) {
        return userRepository.findByRoles_Name(username, pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<Object> save(User user) {
        List<Object> result = new ArrayList<>();
        User checkedUser = userRepository.findByUsername(user.getUsername());
        if (checkedUser == null || (checkedUser.getId().equals(user.getId()))) {
            if (user.getPassword().matches("^(?=.*[\\d])(?=.*[a-z])(?=.*[A-Z]).{8,20}$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setGender(true);
                user.setAccountNonExpired(true);
                user.setAccountNonLocked(true);
                user.setCredentialsNonExpired(true);
                user.setEnabled(true);
                userRepository.save(user);
                result.add("Successfully registered!");
                result.add(HttpStatus.OK);
                return result;
            } else {
                result.add("Password must have at least 8 characters, include uppercase letter, lowercase letter and number character!");
                result.add(HttpStatus.BAD_REQUEST);
                return result;
            }
        } else {
            result.add("Username existed in database!");
            result.add(HttpStatus.BAD_REQUEST);
            return result;
        }
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
