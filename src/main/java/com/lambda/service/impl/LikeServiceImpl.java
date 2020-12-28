package com.lambda.service.impl;

import com.lambda.model.entities.Like;
import com.lambda.model.entities.Song;
import com.lambda.model.entities.User;
import com.lambda.repositories.LikeRepository;
import com.lambda.service.LikeService;
import com.lambda.service.SongService;
import com.lambda.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    SongService songService;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    UserService userService;

    @Override
    public void like(Long id) {
        Optional<Song> song = songService.findById(id);
        User user =  userService.getCurrentUser();
        if (song.isPresent()) {
            Like like = likeRepository.findBySongIdAndUserId(song.get().getId(), user.getId());
            if (like == null) {
                like = new Like(song.get().getId(), user.getId());
                likeRepository.save(like);
            }
        }
    }

    @Override
    public void unlike(Long id) {
        Optional<Song> song = songService.findById(id);
        User currentUser =  userService.getCurrentUser();
        if (song.isPresent()) {
            Like like = likeRepository.findBySongIdAndUserId(song.get().getId(), currentUser.getId());
            if (like != null) {
                likeRepository.delete(like);
            }
        }
    }
}
