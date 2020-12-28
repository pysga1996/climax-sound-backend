package com.lambda.service;

import com.lambda.model.entities.Comment;

import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(Long id);
    void save(Comment comment);
    void deleteById(Long id);
}
