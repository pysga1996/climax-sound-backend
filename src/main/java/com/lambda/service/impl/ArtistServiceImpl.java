package com.lambda.service.impl;

import com.lambda.model.entity.Artist;
import com.lambda.repository.ArtistRepository;
import com.lambda.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ArtistServiceImpl implements ArtistService {
    @Autowired
    ArtistRepository artistRepository;

    @Override
    public Artist findByName(String name){
        return artistRepository.findByName(name);
    }

    @Override
    public Page<Artist> findAllByNameContaining(String name, Pageable pageable){
        return artistRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Artist> findAllByAlbums_Name(String name, Pageable pageable) {
        return artistRepository.findAllByAlbums_Name(name, pageable);
    }

    @Override
    public void save(Artist artist) {
        artistRepository.save(artist);
    }
}
