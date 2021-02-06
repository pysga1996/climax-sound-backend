package com.alpha.service.impl;

import com.alpha.model.entity.Comment;
import com.alpha.repositories.CommentRepository;
import com.alpha.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    public void save(Comment comment) {
        commentRepository.saveAndFlush(comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
