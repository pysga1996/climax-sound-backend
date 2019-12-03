package com.lambda.services.impl;

import com.lambda.models.entities.Artist;
import com.lambda.models.entities.Song;
import com.lambda.models.entities.User;
import com.lambda.models.entities.VerificationToken;
import com.lambda.models.utilities.SearchResponse;
import com.lambda.repositories.UserRepository;
import com.lambda.repositories.VerificationTokenRepository;
import com.lambda.services.ArtistService;
import com.lambda.services.SongService;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private SongService songService;

    @Autowired
    private ArtistService artistService;

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
    public Page<User> findByRoles_Authority(String username, Pageable pageable) {
        return userRepository.findByRoles_Authority(username, pageable);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void setFields(User newUserInfo, User oldUserInfo) {
        newUserInfo.setRoles(oldUserInfo.getRoles());
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
        this.save(user);
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
}
