package com.lambda.services.impl;

import com.lambda.models.entities.Album;
import com.lambda.models.entities.Song;
import com.lambda.repositories.AlbumRepository;
import com.lambda.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
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
    public void setFields(Album oldAlbumInfo, Album newAlbumInfo) {
        oldAlbumInfo.setTitle(newAlbumInfo.getTitle());
        oldAlbumInfo.setArtists(newAlbumInfo.getArtists());
        oldAlbumInfo.setGenres(newAlbumInfo.getGenres());
        oldAlbumInfo.setCountry(newAlbumInfo.getCountry());
        oldAlbumInfo.setReleaseDate(newAlbumInfo.getReleaseDate());
        oldAlbumInfo.setTags(newAlbumInfo.getTags());
        oldAlbumInfo.setTheme(newAlbumInfo.getTheme());
        if(newAlbumInfo.getCoverUrl()!= null) {
            oldAlbumInfo.setCoverUrl(newAlbumInfo.getCoverUrl());
        }
    }

    @Override
    public void save(Album album) {
        albumRepository.saveAndFlush(album);
    }

    @Override
    public void deleteById(Long id) {
        albumRepository.deleteById(id);
    }

    @Override
    public void pushToAlbum(Song song, Long albumId) {
        if (albumId != null) {
            Optional<Album> album = albumRepository.findById(albumId);
            if (album.isPresent()) {
                Collection<Song> songList = album.get().getSongs();
                if (songList == null) {
                    songList = new ArrayList<>();
                }
                songList.add(song);
                album.get().setSongs(songList);
                albumRepository.save(album.get());
            }
        }
    }
}
