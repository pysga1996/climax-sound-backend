package com.lambda.service.impl;

import com.lambda.model.entity.PeopleWhoLiked;
import com.lambda.model.entity.Song;
import com.lambda.model.entity.User;
import com.lambda.repository.PeopleWhoLikedRepository;
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
    PeopleWhoLikedRepository peopleWhoLikedRepository;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Override
    public void like(Long id) {
        Optional<Song> song = songService.findById(id);
        User user =  userDetailService.getCurrentUser();
        if (song.isPresent()) {
            PeopleWhoLiked peopleWhoLiked = peopleWhoLikedRepository.findBySongIdAndUserId(song.get().getId(), user.getId());
            if (peopleWhoLiked == null) {
                peopleWhoLiked = new PeopleWhoLiked(song.get().getId(), user.getId());
                peopleWhoLikedRepository.save(peopleWhoLiked);
            }
        }
    }

    @Override
    public void unlike(Long id) {
        Optional<Song> song = songService.findById(id);
        User currentUser =  userDetailService.getCurrentUser();
        if (song.isPresent()) {
            PeopleWhoLiked peopleWhoLiked = peopleWhoLikedRepository.findBySongIdAndUserId(song.get().getId(), currentUser.getId());
            if (peopleWhoLiked != null) {
                peopleWhoLikedRepository.delete(peopleWhoLiked);
            }
        }
    }
}
