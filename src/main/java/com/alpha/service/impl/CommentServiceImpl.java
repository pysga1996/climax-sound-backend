package com.alpha.service.impl;

import com.alpha.mapper.CommentMapper;
import com.alpha.model.dto.CommentDTO;
import com.alpha.repositories.CommentRepository;
import com.alpha.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findById(Long id) {
        return this.commentRepository.findById(id)
                .map(this.commentMapper::entityToDto);
    }

    @Transactional
    public void save(CommentDTO comment) {
        this.commentRepository.saveAndFlush(this.commentMapper.dtoToEntity(comment));
    }

    @Transactional
    public void deleteById(Long id) {
        this.commentRepository.deleteById(id);
    }
}
