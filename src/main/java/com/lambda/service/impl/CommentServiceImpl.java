package com.lambda.service.impl;

import com.lambda.model.entity.Comment;
import com.lambda.repository.CommentRepository;
import com.lambda.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;

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
