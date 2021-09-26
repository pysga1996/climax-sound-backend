package com.alpha.service.impl;

import com.alpha.constant.EntityType;
import com.alpha.mapper.CommentMapper;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.CommentRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.CommentService;
import com.alpha.service.UserService;
import java.util.Date;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final AlbumRepository albumRepository;

    private final ArtistRepository artistRepository;

    private final SongRepository songRepository;

    private final CommentMapper commentMapper;

    private final UserService userService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
        AlbumRepository albumRepository, ArtistRepository artistRepository,
        SongRepository songRepository, CommentMapper commentMapper, UserService userService) {
        this.commentRepository = commentRepository;
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.commentMapper = commentMapper;
        this.userService = userService;
    }

    @Override
    public Page<CommentDTO> commentList(EntityType type,
        Long entityId, Pageable pageable) {
        return this.commentRepository
            .findAllByEntityTypeAndEntityIdOrderByCreateTimeDesc(type, entityId, pageable)
            .map(this.commentMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findById(Long id) {
        return this.commentRepository.findById(id)
            .map(this.commentMapper::entityToDto);
    }

    @Override
    @Transactional
    public CommentDTO create(CommentDTO commentDTO) {
        switch (commentDTO.getEntityType()) {
            case SONG:
                Optional<Song> optionalSong = this.songRepository
                    .findById(commentDTO.getEntityId());
                if (!optionalSong.isPresent()) {
                    throw new EntityNotFoundException("Song not found!");
                }
                break;
            case ALBUM:
                Optional<Album> optionalAlbum = this.albumRepository
                    .findById(commentDTO.getEntityId());
                if (!optionalAlbum.isPresent()) {
                    throw new EntityNotFoundException("Album not found!");
                }
                break;
            case ARTIST:
                Optional<Artist> optionalArtist = this.artistRepository
                    .findById(commentDTO.getEntityId());
                if (!optionalArtist.isPresent()) {
                    throw new EntityNotFoundException("Artist not found!");
                }
                break;
            default:
                throw new EntityNotFoundException("Comment type is invalid!");
        }
        UserInfo userInfo = this.userService.getCurrentUserInfo();
        Comment commentToSave = this.commentMapper.dtoToEntity(commentDTO);
        commentToSave.setCreateTime(new Date());
        commentToSave.setUserInfo(userInfo);
        commentToSave.setStatus(1);
        this.commentRepository.saveAndFlush(commentToSave);
        return this.commentMapper.entityToDtoPure(commentToSave);
    }

    @Override
    @Transactional
    public CommentDTO update(CommentDTO commentDTO) {
        Optional<Comment> optionalComment = this.commentRepository.findById(commentDTO.getId());
        if (optionalComment.isPresent()) {
            Comment commentToSave = optionalComment.get();
            commentToSave.setContent(commentDTO.getContent());
            commentToSave.setUpdateTime(new Date());
            this.commentRepository.saveAndFlush(commentToSave);
            return this.commentMapper.entityToDtoPure(commentToSave);
        } else {
            throw new EntityNotFoundException("Comment not found!");
        }
    }

    @Transactional
    public void deleteById(Long id, EntityType type) {
        UserInfo currentUserInfo = this.userService.getCurrentUserInfo();
        Optional<Comment> optionalComment = this.commentRepository
            .findByIdAndEntityTypeAndUserInfo(id, type, currentUserInfo);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setStatus(0);
            comment.setUpdateTime(new Date());
            this.commentRepository.saveAndFlush(optionalComment.get());
        } else {
            throw new EntityNotFoundException("Comment not found!");
        }
    }
}
