package com.alpha.service.impl;

import com.alpha.model.entity.Like;
import com.alpha.model.entity.LikeId;
import com.alpha.model.entity.Song;
import com.alpha.repositories.LikeRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.LikeService;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {

    private final SongRepository songRepository;

    private final LikeRepository likeRepository;

    private final UserService userService;

    @Autowired
    public LikeServiceImpl(SongRepository songRepository, LikeRepository likeRepository,
                           UserService userService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void like(Long id) {
        Optional<Song> song = this.songRepository.findById(id);
        OAuth2AuthenticatedPrincipal user = userService.getCurrentUser();
        if (song.isPresent()) {
            Like like = this.likeRepository.findByLikeId_SongIdAndLikeId_Username(song.get().getId(), user.getName());
            if (like == null) {
                like = new Like(new LikeId(song.get().getId(), user.getName()));
                likeRepository.save(like);
            }
        }
    }

    @Override
    @Transactional
    public void unlike(Long id) {
        Optional<Song> song = this.songRepository.findById(id);
        OAuth2AuthenticatedPrincipal currentUser = userService.getCurrentUser();
        if (song.isPresent()) {
            Like like = likeRepository.findByLikeId_SongIdAndLikeId_Username(song.get().getId(), currentUser.getName());
            if (like != null) {
                likeRepository.delete(like);
            }
        }
    }
}
