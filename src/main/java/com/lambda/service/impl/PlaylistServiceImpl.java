package com.lambda.service.impl;

import com.lambda.model.entity.Playlist;
import com.lambda.repository.PlaylistRepository;
import com.lambda.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    @Autowired
    PlaylistRepository playlistRepository;

    @Override
    public Optional<Playlist> findById(Long id) {
        return playlistRepository.findById(id);
    }

    @Override
    public Page<Playlist> findAll(Pageable pageable) {
        return playlistRepository.findAll(pageable);
    }

    @Override
    public Page<Playlist> findAllByNameContaining(String name, Pageable pageable) {
        return playlistRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Playlist playlist) {
        playlistRepository.save(playlist);
    }

    @Override
    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }
}
