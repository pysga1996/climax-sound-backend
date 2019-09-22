package com.lambda.service.impl;

import com.lambda.model.User;
import com.lambda.repository.UserRepository;
import com.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

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
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
