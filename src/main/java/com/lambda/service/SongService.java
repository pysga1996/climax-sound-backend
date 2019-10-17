package com.lambda.service;

import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface SongService {
    Page<Song> findAll(Pageable pageable, String sort);
    Iterable<Song> findAllByTitle(String title);
    Iterable<Song> findAllByTitleContaining(String title);
    Page<Song> findAllByUploader_Id(Long id, Pageable pageable);
    Page<Song> findAllByTitleContaining(String title, Pageable pageable);
    Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable);
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
    Page<Song> setLike(Page<Song> songList);
    Iterable<Song> setLike(Iterable<Song> songList);
}
