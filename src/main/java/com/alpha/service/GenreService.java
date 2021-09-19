package com.alpha.service;

import com.alpha.model.dto.GenreDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface GenreService {

    GenreDTO findById(Integer id);

    Page<GenreDTO> findAll(Pageable pageable);

    Page<GenreDTO> findAllByNameContaining(String name, Pageable pageable);

    GenreDTO findByName(String name);

    GenreDTO create(GenreDTO genre);

    GenreDTO update(Integer id, GenreDTO genre);

    void delete(Integer id);
}
