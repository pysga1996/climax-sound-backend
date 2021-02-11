package com.alpha.service.impl;

import com.alpha.mapper.AlbumMapper;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.service.AlbumService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.helper.UserInfoJsonStringifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final AlbumMapper albumMapper;

    private final UserService userService;

    private final StorageService storageService;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository, AlbumMapper albumMapper,
                            UserService userService, StorageService storageService) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.userService = userService;
        this.storageService = storageService;
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

    public void patchFields(Album oldAlbumInfo, Album newAlbumInfo) {
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

    @Override
    @Transactional
    public void uploadAndSaveAlbum(MultipartFile file, AlbumDTO album) throws IOException {
        Album albumToSave = this.albumMapper.dtoToEntity(album);
        this.albumRepository.save(albumToSave);
        if (file != null) {
            String fileName = this.storageService.upload(file, album);
            albumToSave.setCoverUrl(fileName);
        }
        UserInfo userInfo = UserInfoJsonStringifier.stringify(this.userService.getCurrentUser());
        albumToSave.setUploader(userInfo);
        this.albumRepository.save(albumToSave);
    }

    @Override
    @Transactional
    public boolean editAlbum(MultipartFile file, AlbumDTO album, Long id) throws IOException {
        Optional<Album> oldAlbum = this.albumRepository.findById(id);
        if (oldAlbum.isPresent()) {
            this.patchFields(oldAlbum.get(), this.albumMapper.dtoToEntity(album));
            if (file != null) {
                String fileName = this.storageService.upload(file, oldAlbum.get());
                oldAlbum.get().setCoverUrl(fileName);
            }
            this.albumRepository.save(oldAlbum.get());
            return true;
        } else return false;
    }
}
