package com.lambda.service.impl;

import com.lambda.model.entity.Comment;
import com.lambda.repository.CommentRepository;
import com.lambda.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentRepository commentRepository;

    public void save(Comment comment) {
        commentRepository.saveAndFlush(comment);
    }
}
