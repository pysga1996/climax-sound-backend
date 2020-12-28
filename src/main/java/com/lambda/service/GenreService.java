package com.lambda.service;

import com.lambda.model.entities.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GenreService {
    Optional<Genre> findById(Integer id);
    Page<Genre> findAll(Pageable pageable);
    Genre findByName(String name);
    Iterable<Genre> findAllByNameContaining(String name);
    void save(Genre genre);
    void deleteById(Integer id);
}
