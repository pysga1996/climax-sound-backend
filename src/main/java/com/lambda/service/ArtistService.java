package com.lambda.service;

import com.lambda.model.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface ArtistService {
    Artist findByName(String name);
    Iterable<Artist> findTop10ByNameContaining(String name);
    Page<Artist> findAllByNameContaining(String name, Pageable pageable);
    Page<Artist> findAllByAlbums_Name(String name, Pageable pageable);
    void save(Artist artist);
    String convertToString(Collection<Artist> artists);
    Page<Artist> findAll(Pageable pageable);
}
