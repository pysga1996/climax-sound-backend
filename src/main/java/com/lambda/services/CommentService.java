package com.lambda.services;

import com.lambda.models.entities.Comment;

import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(Long id);
    void save(Comment comment);
    void deleteById(Long id);
}
