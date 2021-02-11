package com.alpha.service;

import com.alpha.model.dto.CommentDTO;

import java.util.Optional;

public interface CommentService {

    Optional<CommentDTO> findById(Long id);

    boolean save(CommentDTO comment, Long songId);

    void deleteById(Long id);
}
