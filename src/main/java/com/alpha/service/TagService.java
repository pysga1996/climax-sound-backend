package com.alpha.service;

import com.alpha.model.dto.TagDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {

    Optional<TagDTO> findById(Long id);

    TagDTO findByName(String name);

    Page<TagDTO> findAll(Pageable pageable);

    Page<TagDTO> findAllByNameContaining(String name, Pageable pageable);

    TagDTO create(TagDTO tagDTO);

    TagDTO update(Long id, TagDTO tagDTO);

    void deleteById(Long id);
}
