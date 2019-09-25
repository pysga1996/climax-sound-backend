package com.lambda.service;

import com.lambda.model.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SongService {
    Song findByName(String name);
    Page<Song> findAllByNameContaining(String name, Pageable pageable);
    Page<Song> findAllByTags_Name(String name, Pageable pageable);
    void save(Song song);
}
