package com.alpha.service;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface SongService {
    Iterable<Song> findAll();

    Page<Song> findAll(Pageable pageable, String sort);

    Iterable<Song> findAllByTitle(String title);

    Iterable<Song> findAllByTitleContaining(String title);

    Page<Song> findAllByUploader_Id(Long id, Pageable pageable);

    Page<Song> findAllByTitleContaining(String title, Pageable pageable);

    Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable);

    Page<Song> findAllByUsersContains(UserDTO user, Pageable pageable);

    Page<Song> findAllByOrderByReleaseDateDesc(Pageable pageable);

    Page<Song> findAllByOrderByListeningFrequencyDesc(Pageable pageable);

    Page<Song> findAllByOrderByDisplayRatingDesc(Pageable pageable);

    Iterable<Song> findTop10By(String sort);

    Page<Song> findAllByLikesCount(Pageable pageable);

    Optional<Song> findById(Long id);

    Iterable<Song> findAllByAlbum_Id(Long id);

    Page<Song> findAllByTag_Name(String name, Pageable pageable);

    Song save(Song song);

    void deleteById(Long id);

    void deleteAll(Collection<Song> songs);

    void setFields(Song oldSongInfo, Song newSongInfo);

    Page<Song> sortByDate(Pageable pageable);

    boolean hasUserLiked(Long songId);

    void setLike(Page<Song> songList);

    void setLike(Iterable<Song> songList);

    void setLike(Song song);
}
