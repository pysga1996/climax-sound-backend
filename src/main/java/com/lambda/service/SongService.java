package com.lambda.service;

import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface SongService {
    Optional<Song> findById(Long id);
    Optional<Song> findByName(String name);
    Iterable<Song> findAllByName(String name);
    Iterable<Song> findAllByNameContaining(String name);
    Page<Song> findAll(Pageable pageable);
    Page<Song> findAllByUploader_Id(Long id, Pageable pageable);
    Page<Song> findAllByNameContaining(String name, Pageable pageable);
    Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable);
    Iterable<Song> findAllByAlbum_Id(Long id);
    Page<Song> findAllByTags_Name(String name, Pageable pageable);
    Song save(Song song);
    Boolean deleteById(Long id);
    void deleteAll(Collection<Song> songs);
    void setFields(Song oldSongInfo, Song newSongInfo);
    Page<Song> sortByDate(Pageable pageable);
    boolean hasUserLiked(Long songId);
    Page<Song> setLike(Page<Song> songList);
}
