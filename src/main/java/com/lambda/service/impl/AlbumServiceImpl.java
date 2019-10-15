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
    public Iterable<Album> findAllByTitle(String title) {
        return albumRepository.findAllByTitle(title);
    }

    @Override
    public Page<Album> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    @Override
    public Page<Album> findAllByTitleContaining(String title, Pageable pageable) {
        return albumRepository.findAllByTitleContaining(title, pageable);
    }

    @Override
    public Page<Album> findAllByArtist_Name(String title, Pageable pageable) {
        return albumRepository.findAllByArtist_Name(title, pageable);
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
