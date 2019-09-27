package com.lambda.service;

import com.lambda.model.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    Optional<Album> findById(Long id);

    Album findByName(String name);

    Page<Album> findAll(Pageable pageable);

    Page<Album> findAllByNameContaining(String name, Pageable pageable);

    Page<Album> findAllByArtists_Name(String name, Pageable pageable);

    void save(Album album);

    void deleteById(Long id);
}
