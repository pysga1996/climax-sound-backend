package com.lambda.service;

import com.lambda.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByRoles_Name(String username, Pageable pageable);
    Optional<User> findById(Long id);
    List<Object> save(User user);
    void deleteById(Long id);
}
