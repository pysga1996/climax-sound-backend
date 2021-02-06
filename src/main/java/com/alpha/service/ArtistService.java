package com.alpha.service;

import com.alpha.model.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface ArtistService {
    Optional<Artist> findById(Long id);

    Artist findByName(String name);

    Iterable<Artist> findTop10ByNameContaining(String name);

    Page<Artist> findAllByNameContaining(String name, Pageable pageable);

    Page<Artist> findAllByAlbums_Name(String name, Pageable pageable);

    void save(Artist artist);

    String convertToString(Collection<Artist> artists);

    Page<Artist> findAll(Pageable pageable);

    void setFields(Artist oldArtistInfo, Artist newArtistInfo);

    Iterable<Artist> findAllByNameContaining(String name);

    void deleteById(Long id);
}
