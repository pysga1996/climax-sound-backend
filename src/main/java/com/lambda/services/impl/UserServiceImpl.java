package com.lambda.services.impl;

import com.lambda.models.entities.*;
import com.lambda.models.utilities.SearchResponse;
import com.lambda.repositories.PasswordResetTokenRepository;
import com.lambda.repositories.SettingRepository;
import com.lambda.repositories.UserRepository;
import com.lambda.repositories.VerificationTokenRepository;
import com.lambda.services.ArtistService;
import com.lambda.services.RoleService;
import com.lambda.services.SongService;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    @Override
    public User getCurrentUser() {
        User user;
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        if (userService.findByUsername(username).isPresent()) {
            user = userService.findByUsername(username).get();
        } else {
            user = new User();
            user.setUsername("Anonymous");
        }
        return user;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> findByUsernameContaining(String username, Pageable pageable) {
        return userRepository.findByUsernameContaining(username, pageable);
    }

    @Override
    public Page<User> findByRoles_Authority(String authority, Pageable pageable) {
        return userRepository.findByAuthorities_Authority(authority, pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User save(User user, boolean createAction) {
        if (createAction) {
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                return null;
            } else {
                Role role = roleService.findByAuthority(DEFAULT_ROLE);
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                user.setAuthorities(roles);
                userRepository.saveAndFlush(user);
                userService.createSetting(user);
                return user;
            }
        } else {
            userRepository.saveAndFlush(user);
            return user;
        }
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void setFields(User newUserInfo, User oldUserInfo) {
        newUserInfo.setAuthorities(oldUserInfo.getAuthorities());
        newUserInfo.setAccountNonExpired(oldUserInfo.isAccountNonExpired());
        newUserInfo.setAccountNonLocked(oldUserInfo.isAccountNonLocked());
        newUserInfo.setCredentialsNonExpired(oldUserInfo.isCredentialsNonExpired());
        newUserInfo.setEnabled(oldUserInfo.isEnabled());
        newUserInfo.setFavoriteSongs(oldUserInfo.getFavoriteSongs());
        newUserInfo.setFavoriteAlbums(oldUserInfo.getFavoriteAlbums());
        newUserInfo.setComments(oldUserInfo.getComments());
    }

    @Override
    public void setFieldsEdit(User oldUserInfo, User newUserInfo) {
        oldUserInfo.setFirstName(newUserInfo.getFirstName());
        oldUserInfo.setLastName(newUserInfo.getLastName());
        oldUserInfo.setBirthDate(newUserInfo.getBirthDate());
        oldUserInfo.setGender(newUserInfo.getGender());
        oldUserInfo.setPhoneNumber(newUserInfo.getPhoneNumber());
        if (!oldUserInfo.getPassword().equals(newUserInfo.getPassword())) {
            oldUserInfo.setPassword(passwordEncoder.encode(newUserInfo.getPassword()));
        }
        if (newUserInfo.getAvatarUrl() != null) {
            oldUserInfo.setAvatarUrl(newUserInfo.getAvatarUrl());
        }
    }

    @Override
    public User setInfo(Long id, User currentUser) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent() && !user.get().getId().equals(currentUser.getId())) {
            return new User(user.get().getFirstName(), user.get().getLastName(), user.get().getGender(), user.get().getAvatarUrl());
        } else if (user.isPresent() && user.get().getId().equals(currentUser.getId())) {
            return currentUser;
        } else return null;
    }

    @Override
    public SearchResponse search(String searchText) {
        Iterable<Song> songs = songService.findAllByTitleContaining(searchText);
        Iterable<Artist> artists = artistService.findAllByNameContaining(searchText);
        return new SearchResponse(songs, artists);
    }

    private boolean emailExist(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public void getVerificationToken(String token) throws Exception {
        if (token == null) {
            throw new InvalidTokenException("1");
        }
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new InvalidTokenException("1");
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new InvalidTokenException("2");
        }
        user.setEnabled(true);
        userRepository.saveAndFlush(user);
        this.removeToken(verificationToken);
    }

    @Override
    public User findUserByToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public void removeToken(VerificationToken token) {
        tokenRepository.delete(token);
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public void validatePasswordResetToken(long id, String token) throws Exception {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            throw new InvalidTokenException("1");
        }
        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new InvalidTokenException("2");
        }
        User user = passToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public void createSetting(User user) {
        Setting setting = new Setting();
        setting.setUser(user);
        settingRepository.save(setting);
    }

    @Override
    public void changeSetting(Setting setting) {
        setting.setUser(this.getCurrentUser());
        this.settingRepository.save(setting);
    }
}
