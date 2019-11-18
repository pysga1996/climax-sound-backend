package com.lambda.services.impl;

import com.lambda.models.entities.Artist;
import com.lambda.repositories.ArtistRepository;
import com.lambda.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {
    @Autowired
    ArtistRepository artistRepository;

    @Override
    public Optional<Artist> findById(Long id) {
        return artistRepository.findById(id);
    }

    @Override
    public Artist findByName(String name) {
        return artistRepository.findByName(name);
    }

    @Override
    public Iterable<Artist> findTop10ByNameContaining(String name) {
        return artistRepository.findFirst10ByNameContaining(name);
    }

    @Override
    public Page<Artist> findAllByNameContaining(String name, Pageable pageable) {
        return artistRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Artist> findAllByAlbums_Name(String title, Pageable pageable) {
        return artistRepository.findAllByAlbums_Title(title, pageable);
    }

    @Override
    public void save(Artist artist) {
        artistRepository.saveAndFlush(artist);
    }

    @Override
    public Page<Artist> findAll(Pageable pageable) {
        return artistRepository.findAll(pageable);
    }

    public String convertToString(Collection<Artist> artists) {
        String artistsString = "";
        if (!artists.isEmpty()) {
            artistsString = " - ";
            for (Artist artist : artists) {
                artistsString = artistsString.concat(artist.getName()).concat("_");
            }
        }
        return artistsString;
    }
    @Override
    public void setFields(Artist oldArtistInfo, Artist newArtistInfo) {
        oldArtistInfo.setName(newArtistInfo.getName());
        oldArtistInfo.setBiography(newArtistInfo.getBiography());
        oldArtistInfo.setBirthDate(newArtistInfo.getBirthDate());
        if(newArtistInfo.getAvatarUrl()!= null) {
            oldArtistInfo.setAvatarUrl(newArtistInfo.getAvatarUrl());
        }
    }
    @Override
    public Iterable<Artist> findAllByNameContaining(String name) {
        return artistRepository.findAllByNameContaining(name);
    }

    @Override
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }
}
