package com.lambda.service;

import com.lambda.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AlbumService {
    Album findByName(String name);
    Page<Album> findAllByNameContaining(String name, Pageable pageable);
    Page<Album> findAllByArtists_Name(String name, Pageable pageable);
}
