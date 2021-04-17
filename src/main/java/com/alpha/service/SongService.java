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

    Iterable<SongDTO> findAll();

    Page<SongDTO> findAll(Pageable pageable, String sort);

    Iterable<SongDTO> findAllByTitle(String title);

    Iterable<SongDTO> findAllByTitleContaining(String title);

    Page<SongDTO> findAllByUploader(Pageable pageable);

    Page<SongDTO> findAllByTitleContaining(String title, Pageable pageable);

    Page<SongDTO> findAllByArtistsContains(ArtistDTO artist, Pageable pageable);

    Page<SongDTO> findAllByUsersContains(Pageable pageable);

    Page<SongDTO> findAllByOrderByReleaseDateDesc(Pageable pageable);

    Page<SongDTO> findAllByOrderByListeningFrequencyDesc(Pageable pageable);

    Page<SongDTO> findAllByOrderByDisplayRatingDesc(Pageable pageable);

    Iterable<SongDTO> findTop10By(String sort);

    Page<SongDTO> findAllByLikesCount(Pageable pageable);

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
