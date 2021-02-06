package com.alpha.service.impl;

import com.alpha.model.entity.Genre;
import com.alpha.repositories.GenreRepository;
import com.alpha.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Genre> findById(Integer id) {
        return genreRepository.findById(id);
    }

    @Override
    public Page<Genre> findAll(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    public Iterable<Genre> findAllByNameContaining(String name) {
        return genreRepository.findAllByNameContaining(name);
    }

    @Override
    public void save(Genre genre) {
        genreRepository.save(genre);
    }

    @Override
    public void deleteById(Integer id) {
        genreRepository.deleteById(id);
    }
}
