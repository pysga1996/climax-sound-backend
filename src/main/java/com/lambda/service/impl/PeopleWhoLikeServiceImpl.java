package com.lambda.service.impl;

import com.lambda.model.entity.Like;
import com.lambda.model.entity.Song;
import com.lambda.model.entity.User;
import com.lambda.repository.LikeRepository;
import com.lambda.service.PeopleWhoLikedService;
import com.lambda.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PeopleWhoLikeServiceImpl implements PeopleWhoLikedService {
    @Autowired
    SongService songService;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Override
    public void like(Long id) {
        Optional<Song> song = songService.findById(id);
        User user =  userDetailService.getCurrentUser();
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
        User currentUser =  userDetailService.getCurrentUser();
        if (song.isPresent()) {
            Like like = likeRepository.findBySongIdAndUserId(song.get().getId(), currentUser.getId());
            if (like != null) {
                likeRepository.delete(like);
            }
        }
    }
}
