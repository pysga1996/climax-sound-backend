package com.alpha.service;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.SongDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface SongService {

    Page<SongDTO> findAll(Pageable pageable);

    Page<SongDTO> findAllByConditions(Pageable pageable, SongSearchDTO songSearchDTO);

    Optional<SongDTO> findById(Long id);

    Iterable<SongDTO> findAllByAlbum_Id(Long id);

    Page<SongDTO> findAllByTag_Name(String name, Pageable pageable);

    SongDTO save(SongDTO song);

    void deleteById(Long id);

    void deleteAll(Collection<SongDTO> songs);

    void setFields(SongDTO oldSongInfo, SongDTO newSongInfo);

    Page<SongDTO> sortByDate(Pageable pageable);

    boolean hasUserLiked(Long songId);

    void setLike(Page<SongDTO> songList);

    void setLike(Iterable<SongDTO> songList);

    void setLike(SongDTO song);

    void uploadAndSaveSong(MultipartFile file, SongDTO song, Long albumId) throws IOException;
}
