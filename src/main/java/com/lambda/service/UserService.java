package com.lambda.service;

import com.lambda.model.entities.Setting;
import com.lambda.model.entities.User;
import com.lambda.model.entities.VerificationToken;
import com.lambda.model.utilities.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    User getCurrentUser();
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    Page<User> findByRoles_Authority(String username, Pageable pageable);
    Optional<User> findById(Long id);
    User findByEmail(String email);
    User save(User user, boolean createAction);
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
    void createSetting(User user);
    void changeSetting(Setting setting);
}
