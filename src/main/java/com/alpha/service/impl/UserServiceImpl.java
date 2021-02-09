package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDTO) {
            return (UserDTO) authentication.getPrincipal();
        }
        return null;
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return Optional.empty();
    }

}
