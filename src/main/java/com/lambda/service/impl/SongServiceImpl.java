package com.lambda.service.impl;

import com.lambda.model.entity.Song;
import com.lambda.repository.SongRepository;
import com.lambda.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    SongRepository songRepository;

    @Autowired
    AudioStorageService audioStorageService;

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public Optional<Song> findByName(String name) {
        return songRepository.findByName(name);
    }

    @Override
    public Iterable<Song> findAllByName(String name) {
        return songRepository.findAllByName(name);
    }

    @Override
    public Iterable<Song> findAllByNameContaining(String name) {
        return songRepository.findAllByNameContaining(name);
    }

    @Override
    public Page<Song> findAll(Pageable pageable) {
        return songRepository.findAll(pageable);
    }

    @Override
    public Page<Song> findAllByNameContaining(String name, Pageable pageable) {
        return songRepository.findAllByNameContaining(name, pageable);
    }

    @Override
    public Page<Song> findAllByAlbum_Id(Long id, Pageable pageable) {
        return songRepository.findAllByAlbum_Id(id, pageable);
    }

    @Override
    public Page<Song> findAllByTags_Name(String name, Pageable pageable) {
        return songRepository.findAllByTags_Name(name, pageable);
    }

    @Override
    public Iterable<Song> findAllByAlbum_Id(Long id) {
        return songRepository.findAllByAlbum_Id(id);
    }

    @Override
    public Song save(Song song) {
        songRepository.saveAndFlush(song);
        return song;
    }

    @Override
    public Boolean deleteById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            songRepository.deleteById(id);
            String fileUrl = song.get().getUrl();
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            return audioStorageService.deleteLocalStorageFile(audioStorageService.audioStorageLocation, filename);
        }
        return false;
    }

    @Override
    public void deleteAll(Collection<Song> songs) {
        songRepository.deleteAll(songs);
    }
}
