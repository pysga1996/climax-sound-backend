package com.lambda.service.impl;

import com.lambda.model.Genre;
import com.lambda.repository.GenreRepository;
import com.lambda.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl implements GenreService {
    @Autowired
    GenreRepository genreRepository;

    @Override
    public Genre findByName(String name) {
        return genreRepository.findByName(name);
    }

    @Override
    public Iterable<Genre> findAllByNameContaining(String name) {
        return genreRepository.findAllByNameContaining(name);
    }
}
