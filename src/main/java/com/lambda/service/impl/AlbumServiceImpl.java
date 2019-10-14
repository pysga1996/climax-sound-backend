package com.lambda.service.impl;

import com.lambda.model.entity.Album;
import com.lambda.repository.AlbumRepository;
import com.lambda.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    AlbumRepository albumRepository;

    @Override
    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }

    @Override
    public Iterable<Album> findByName(String name) {
        return albumRepository.findByName(name);
    }

    @Override
    public Page<Album> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    @Override
    public Page<Album> findAllByNameContaining(String name, Pageable pageable) {
        return albumRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Album> findAllByArtists_Name(String name, Pageable pageable) {
        return albumRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public void save(Album album) {
        albumRepository.saveAndFlush(album);
    }

    @Override
    public void deleteById(Long id) {
        albumRepository.deleteById(id);
    }
}
