package com.alpha.service;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.SongDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AlbumService {

    Optional<AlbumDTO> findById(Long id);

    Iterable<AlbumDTO> findAllByTitle(String name);

    Page<AlbumDTO> findAll(Pageable pageable);

    Page<AlbumDTO> findAllByTitleContaining(String name, Pageable pageable);

    Page<AlbumDTO> findAllByArtist_Name(String name, Pageable pageable);

    void setFields(AlbumDTO oldAlbumInfo, AlbumDTO newAlbumInfo);

    void save(AlbumDTO album);

    void deleteById(Long id);
}
