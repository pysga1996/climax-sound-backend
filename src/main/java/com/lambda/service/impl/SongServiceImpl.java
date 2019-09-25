package com.lambda.service.impl;

import com.lambda.model.Song;
import com.lambda.repository.SongRepository;
import com.lambda.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    SongRepository songRepository;

    @Override
    public Song findByName(String name) {
        return songRepository.findByName(name);
    }

    @Override
    public Page<Song> findAllByNameContaining(String name, Pageable pageable) {
        return songRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Song> findAllByTags_Name(String name, Pageable pageable) {
        return songRepository.findAllByTags_Name(name, pageable);
    }

    @Override
    public void save(Song song) {
        songRepository.save(song);
    }
}
