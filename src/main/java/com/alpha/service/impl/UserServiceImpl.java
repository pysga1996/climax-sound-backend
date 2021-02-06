package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDTO getCurrentUser() {
        return null;
    }

    @Override
    public Optional<UserDTO> findByUsername(String username) {
        return Optional.empty();
    }

}
