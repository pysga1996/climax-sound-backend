package com.alpha.service;

import com.alpha.model.dto.TagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TagService {

    Optional<TagDTO> findById(Long id);

    TagDTO findByName(String name);

    Page<TagDTO> findAll(Pageable pageable);

    Page<TagDTO> findAllByNameContaining(String name, Pageable pageable);

    void save(TagDTO tag);

    void deleteById(Long id);
}
