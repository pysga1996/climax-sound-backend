package com.alpha.service;

import com.alpha.model.dto.ArtistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ArtistService {
    Optional<ArtistDTO> findById(Long id);

    ArtistDTO findByName(String name);

    Iterable<ArtistDTO> findTop10ByNameContaining(String name);

    Page<ArtistDTO> findAllByNameContaining(String name, Pageable pageable);

    Page<ArtistDTO> findAllByAlbums_Name(String name, Pageable pageable);

    void save(ArtistDTO artist);

    Page<ArtistDTO> findAll(Pageable pageable);

    void setFields(ArtistDTO oldArtistInfo, ArtistDTO newArtistInfo);

    Iterable<ArtistDTO> findAllByNameContaining(String name);

    void deleteById(Long id);
}
