package com.lambda.configuration.security;

import com.lambda.model.entity.Playlist;
import com.lambda.model.entity.User;
import com.lambda.service.PlaylistService;
import com.lambda.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
public class WebSecurity {
    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    PlaylistService playlistService;

    public boolean checkUserId(Long id) {
        System.out.println(id);
//        Optional<Playlist> playlist = playlistService.findById(Long.parseLong(id));
        Optional<Playlist> playlist = playlistService.findById(id);
        User currentUser = userDetailService.getCurrentUser();
        if (playlist.isPresent() && currentUser.getId()!=null) {
            return playlist.get().getUser().getId().equals(currentUser.getId());
        }
        return false;
    }
}
