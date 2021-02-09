package com.alpha.service.impl;

import com.alpha.mapper.AlbumMapper;
import com.alpha.mapper.SongMapper;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Song;
import com.alpha.repositories.AlbumRepository;
import com.alpha.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;

    private final SongMapper songMapper;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, AlbumMapper albumMapper, SongMapper songMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.songMapper = songMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AlbumDTO> findById(Long id) {
        return this.albumRepository.findById(id)
                .map(this.albumMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<AlbumDTO> findAllByTitle(String title) {
        return StreamSupport
                .stream(this.albumRepository.findAllByTitle(title).spliterator(), false)
                .map(this.albumMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDTO> findAll(Pageable pageable) {
        Page<Album> albumPage = this.albumRepository.findAll(pageable);
        return new PageImpl<>(albumPage.get()
                .map(this.albumMapper::entityToDto)
                .collect(Collectors.toList()), pageable, albumPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDTO> findAllByTitleContaining(String title, Pageable pageable) {
        Page<Album> albumPage = this.albumRepository.findAllByTitleContaining(title, pageable);
        return new PageImpl<>(albumPage.get()
                .map(this.albumMapper::entityToDto)
                .collect(Collectors.toList()), pageable, albumPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDTO> findAllByArtist_Name(String title, Pageable pageable) {
        Page<Album> albumPage = this.albumRepository.findAllByArtist_Name(title, pageable);
        return new PageImpl<>(albumPage.get()
                .map(this.albumMapper::entityToDto)
                .collect(Collectors.toList()), pageable, albumPage.getTotalElements());
    }

    @Override
    public void setFields(AlbumDTO oldAlbumInfo, AlbumDTO newAlbumInfo) {
        oldAlbumInfo.setTitle(newAlbumInfo.getTitle());
        oldAlbumInfo.setArtists(newAlbumInfo.getArtists());
        oldAlbumInfo.setGenres(newAlbumInfo.getGenres());
        oldAlbumInfo.setCountry(newAlbumInfo.getCountry());
        oldAlbumInfo.setReleaseDate(newAlbumInfo.getReleaseDate());
        oldAlbumInfo.setTags(newAlbumInfo.getTags());
        if (newAlbumInfo.getCoverUrl() != null) {
            oldAlbumInfo.setCoverUrl(newAlbumInfo.getCoverUrl());
        }
    }

    @Override
    @Transactional
    public void save(AlbumDTO album) {
        this.albumRepository.saveAndFlush(this.albumMapper.dtoToEntity(album));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        albumRepository.deleteById(id);
    }


}
