package com.lambda.service.impl;

import com.lambda.model.Album;
import com.lambda.repository.AlbumRepository;
import com.lambda.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    AlbumRepository albumRepository;

    @Override
    public Album findByName(String name) {
        return albumRepository.findByName(name);
    }

    @Override
    public Page<Album> findAllByNameContaining(String name, Pageable pageable) {
        return albumRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Album> findAllByArtists_Name(String name, Pageable pageable) {
        return albumRepository.findAllByNameContaining(name, pageable);
    }
}
