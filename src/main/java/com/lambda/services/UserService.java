package com.lambda.services;

import com.lambda.models.entities.User;
import com.lambda.models.entities.VerificationToken;
import com.lambda.models.utilities.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByRoles_Authority(String username, Pageable pageable);
    Optional<User> findById(Long id);
    User findByEmail(String email);
    void save(User user);
    void deleteById(Long id);
    void setFields(User newUserInfo, User oldUserInfo);
    void setFieldsEdit(User oldUserInfo, User newUserInfo);
    User setInfo(Long id, User currentUser);
    SearchResponse search(String searchText);
    void createVerificationToken(User user, String token);
    void getVerificationToken(String VerificationToken) throws Exception;
    User findUserByToken(String verificationToken);
    void removeToken(VerificationToken token);
    void createPasswordResetToken(User user, String token);
    void validatePasswordResetToken(long id, String token) throws Exception;
    void changeUserPassword(User user, String password);
}
