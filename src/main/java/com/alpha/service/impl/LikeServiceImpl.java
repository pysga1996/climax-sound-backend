package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Like;
import com.alpha.model.entity.Song;
import com.alpha.repositories.LikeRepository;
import com.alpha.service.LikeService;
import com.alpha.service.SongService;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private final SongService songService;

    private final LikeRepository likeRepository;

    private final UserService userService;

    @Autowired
    public LikeServiceImpl(SongService songService, LikeRepository likeRepository, UserService userService) {
        this.songService = songService;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    public void like(Long id) {
        Optional<Song> song = songService.findById(id);
        UserDTO user = userService.getCurrentUser();
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
        UserDTO currentUser = userService.getCurrentUser();
        if (song.isPresent()) {
            Like like = likeRepository.findBySongIdAndUserId(song.get().getId(), currentUser.getId());
            if (like != null) {
                likeRepository.delete(like);
            }
        }
    }
}
