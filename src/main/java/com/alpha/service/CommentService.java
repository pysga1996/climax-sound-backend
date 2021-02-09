package com.alpha.service;

import com.alpha.model.dto.CommentDTO;

import java.util.Optional;

public interface CommentService {

    Optional<CommentDTO> findById(Long id);

    void save(CommentDTO comment);

    void deleteById(Long id);
}
