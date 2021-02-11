package com.alpha.service.impl;

import com.alpha.mapper.CommentMapper;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Song;
import com.alpha.repositories.CommentRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.CommentService;
import com.alpha.service.UserService;
import com.alpha.util.helper.UserInfoJsonStringifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final SongRepository songRepository;

    private final CommentMapper commentMapper;

    private final UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, SongRepository songRepository,
                              CommentMapper commentMapper, UserService userService) {
        this.commentRepository = commentRepository;
        this.songRepository = songRepository;
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findById(Long id) {
        return this.commentRepository.findById(id)
                .map(this.commentMapper::entityToDto);
    }

    @Transactional
    public boolean save(CommentDTO comment, Long songId) {
        Optional<Song> song = this.songRepository.findById(songId);
        if (!song.isPresent()) return false;
        LocalDateTime localDateTime = LocalDateTime.now();
        UserDTO currentUser = this.userService.getCurrentUser();
        Comment commentToSave = this.commentMapper.dtoToEntity(comment);
        commentToSave.setLocalDateTime(localDateTime);
        commentToSave.setSong(song.get());
        commentToSave.setUserInfo(UserInfoJsonStringifier.stringify(currentUser));
        this.commentRepository.save(commentToSave);
        return true;
    }

    @Transactional
    public void deleteById(Long id) {
        this.commentRepository.deleteById(id);
    }
}
