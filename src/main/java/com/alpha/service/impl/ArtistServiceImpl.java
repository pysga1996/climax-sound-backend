package com.alpha.service.impl;

import com.alpha.mapper.ArtistMapper;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.entity.Artist;
import com.alpha.repositories.ArtistRepository;
import com.alpha.service.ArtistService;
import com.alpha.util.formatter.StringAccentRemover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtistDTO> findById(Long id) {
        return artistRepository.findById(id).map(this.artistMapper::entityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO findByName(String name) {
        return this.artistMapper.entityToDto(artistRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ArtistDTO> findTop10ByNameContaining(String name) {
        Iterable<Artist> top10List;
        if (name.equals(StringAccentRemover.removeStringAccent(name))) {
            top10List = this.artistRepository.findFirst10ByUnaccentNameContainingIgnoreCase(name);
        } else {
            top10List = this.artistRepository.findFirst10ByNameContainingIgnoreCase(name);
        }
        return StreamSupport
                .stream(top10List.spliterator(), false)
                .map(this.artistMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findAllByNameContaining(String name, Pageable pageable) {
        Page<Artist> artistPage = this.artistRepository.findAllByNameContaining(name, pageable);
        return new PageImpl<>(artistPage.getContent()
                .stream()
                .map(this.artistMapper::entityToDto)
                .collect(Collectors.toList()), pageable, artistPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findAllByAlbums_Name(String title, Pageable pageable) {
        Page<Artist> artistPage = this.artistRepository.findAllByAlbums_Title(title, pageable);
        return new PageImpl<>(artistPage.getContent()
                .stream()
                .map(this.artistMapper::entityToDto)
                .collect(Collectors.toList()), pageable, artistPage.getTotalElements());
    }

    @Override
    @Transactional
    public void save(ArtistDTO artist) {
        String unaccentName = StringAccentRemover.removeStringAccent(artist.getName());
        artist.setUnaccentName(unaccentName.toLowerCase());
        artistRepository.saveAndFlush(this.artistMapper.dtoToEntity(artist));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findAll(Pageable pageable) {
        Page<Artist> artistPage = this.artistRepository.findAll(pageable);
        return new PageImpl<>(artistPage.getContent()
                .stream()
                .map(this.artistMapper::entityToDto)
                .collect(Collectors.toList()), pageable, artistPage.getTotalElements());
    }

    @Override
    public void setFields(ArtistDTO oldArtistInfo, ArtistDTO newArtistInfo) {
        oldArtistInfo.setName(newArtistInfo.getName());
        oldArtistInfo.setBiography(newArtistInfo.getBiography());
        oldArtistInfo.setBirthDate(newArtistInfo.getBirthDate());
        if (newArtistInfo.getAvatarUrl() != null) {
            oldArtistInfo.setAvatarUrl(newArtistInfo.getAvatarUrl());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<ArtistDTO> findAllByNameContaining(String name) {
        Iterable<Artist> artistIterableList;
        if (name.equals(StringAccentRemover.removeStringAccent(name))) {
            artistIterableList = artistRepository.findAllByUnaccentNameContainingIgnoreCase(name);
        } else {
            artistIterableList = artistRepository.findAllByNameContainingIgnoreCase(name);
        }
        return StreamSupport
                .stream(artistIterableList.spliterator(), false)
                .map(this.artistMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }
}
