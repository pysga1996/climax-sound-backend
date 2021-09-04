package com.alpha.service.impl;

import com.alpha.mapper.CommentMapper;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.CommentRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.CommentService;
import com.alpha.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final SongRepository songRepository;

    private final CommentMapper commentMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
        SongRepository songRepository, CommentMapper commentMapper,
        UserInfoMapper userInfoMapper, UserService userService) {
        this.commentRepository = commentRepository;
        this.songRepository = songRepository;
        this.commentMapper = commentMapper;
        this.userInfoMapper = userInfoMapper;
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findById(Long id) {
        return this.commentRepository.findById(id)
            .map(this.commentMapper::entityToDto);
    }

    @Transactional
    public CommentDTO save(CommentDTO commentDTO, Long songId) {
        Optional<Song> song = this.songRepository.findById(songId);
        if (!song.isPresent()) {
            throw new EntityNotFoundException("Song not found!");
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        UserInfoDTO userInfoDTO = this.userService.getCurrentProfile();
        UserInfo userInfo = this.userInfoMapper.dtoToEntity(userInfoDTO);
        Comment commentToSave = this.commentMapper.dtoToEntity(commentDTO);
        commentToSave.setLocalDateTime(localDateTime);
        commentToSave.setSong(song.get());
        commentToSave.setUserInfo(userInfo);
        this.commentRepository.saveAndFlush(commentToSave);
        return this.commentMapper.entityToDtoPure(commentToSave);
    }

    @Transactional
    public void deleteById(Long id) {
        String username = this.userService.getCurrentUsername();
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent() && comment.get().getUserInfo().getUsername()
            .equals(username)) {
            this.commentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Comment not found!");
        }
    }
}
