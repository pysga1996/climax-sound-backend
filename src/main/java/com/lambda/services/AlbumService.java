package com.lambda.services;

import com.lambda.models.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {
    Optional<Album> findById(Long id);
    Iterable<Album> findAllByTitle(String name);
    Page<Album> findAll(Pageable pageable);
    Page<Album> findAllByTitleContaining(String name, Pageable pageable);
    Page<Album> findAllByArtist_Name(String name, Pageable pageable);
    void setFields(Album oldAlbumInfo, Album newAlbumInfo);
    void save(Album album);
    void deleteById(Long id);
}
