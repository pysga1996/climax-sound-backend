package com.lambda.services;

import com.lambda.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByRoles_Name(String username, Pageable pageable);
    Optional<User> findById(Long id);
    void save(User user);
    void deleteById(Long id);
    void setFields(User newUserInfo, User oldUserInfo);
    void setFieldsEdit(User oldUserInfo, User newUserInfo);
    User setInfo(Long id, User currentUser);
}
