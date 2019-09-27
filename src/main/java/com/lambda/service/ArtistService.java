package com.lambda.service;

import com.lambda.model.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {
    Artist findByName(String name);
    Page<Artist> findAllByNameContaining(String name, Pageable pageable);
    Page<Artist> findAllByAlbums_Name(String name, Pageable pageable);
    void save(Artist artist);
}
