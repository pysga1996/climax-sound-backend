package com.alpha.service;

import com.alpha.model.dto.GenreDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface GenreService {

    Optional<GenreDTO> findById(Integer id);

    Page<GenreDTO> findAll(Pageable pageable);

    GenreDTO findByName(String name);

    Iterable<GenreDTO> findAllByNameContaining(String name);

    void save(GenreDTO genre);

    void deleteById(Integer id);
}
