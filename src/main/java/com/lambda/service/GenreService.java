package com.lambda.service;

import com.lambda.model.Genre;
import org.springframework.stereotype.Service;

@Service
public interface GenreService {
    Genre findByName(String name);
    Iterable<Genre> findAllByNameContaining(String name);
}
