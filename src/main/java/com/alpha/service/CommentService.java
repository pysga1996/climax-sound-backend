package com.alpha.service;

import com.alpha.constant.CommentType;
import com.alpha.model.dto.CommentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Page<CommentDTO> commentList(CommentType type, Long entityId, Pageable pageable);

    Optional<CommentDTO> findById(Long id);

    CommentDTO create(CommentDTO commentDTO);

    CommentDTO update(CommentDTO commentDTO);

    void deleteById(Long id, CommentType type);
}
