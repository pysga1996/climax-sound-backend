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
import com.alpha.util.helper.UserInfoJsonStringifier;
import org.springframework.beans.factory.annotation.Autowired;
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
    public boolean save(CommentDTO comment, Long songId) {
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
        commentToSave.setUserInfo(UserInfoJsonStringifier.stringify(currentUserShortInfo));
        this.commentRepository.save(commentToSave);
        return true;
    }

    @Transactional
    public void deleteById(Long id) {
        this.commentRepository.deleteById(id);
    }
}
