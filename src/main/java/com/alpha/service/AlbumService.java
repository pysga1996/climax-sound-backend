package com.alpha.service;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.SongDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface AlbumService {

    Optional<AlbumDTO> findById(Long id);

    Iterable<AlbumDTO> findAllByTitle(String name);

    Page<AlbumDTO> findAll(Pageable pageable);

    Page<AlbumDTO> findAllByConditions(Pageable pageable, AlbumSearchDTO albumSearchDTO);

    Page<AlbumDTO> findAllByArtist_Name(String name, Pageable pageable);

    void save(AlbumDTO album);

    void deleteById(Long id);

    void uploadAndSaveAlbum(MultipartFile file, AlbumDTO album) throws IOException;

    boolean editAlbum(MultipartFile file, AlbumDTO album, Long id) throws IOException;
}
