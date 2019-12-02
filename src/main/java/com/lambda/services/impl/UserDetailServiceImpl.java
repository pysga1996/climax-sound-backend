package com.lambda.services.impl;

import com.lambda.models.utilities.CustomUserDetails;
import com.lambda.models.entities.Role;
import com.lambda.models.entities.User;
import com.lambda.repositories.UserRepository;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByUsername(username);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (user.isPresent()) {
            Collection<Role> roles = user.get().getRoles();
            for (Role role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getAuthority()));
            }
            return new CustomUserDetails(user.get().getUsername(), user.get().getPassword(), grantedAuthorities);
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

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

//    public boolean checkLogin(User user) {
//        Iterable<User> users = userRepository.findAll();
//        boolean isCorrectUser = false;
//        for (User currentUser: users) {
//            if (currentUser.getUsername().equals(user.getUsername())) {
//                isCorrectUser = true;
//            }
//        }
//        return isCorrectUser;
//    }

}