package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.EntityType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.mapper.ArtistMapper;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.ArtistService;
import com.alpha.service.FavoritesService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
public class ArtistServiceImpl implements ArtistService {

    @Value("${storage.storage-type}")
    private StorageType storageType;

    private final ArtistRepository artistRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final ArtistMapper artistMapper;

    private final StorageService storageService;

    private final UserService userService;

    private final FavoritesService favoritesService;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository,
        ResourceInfoRepository resourceInfoRepository,
        ArtistMapper artistMapper, StorageService storageService,
        UserService userService, FavoritesService favoritesService) {
        this.artistRepository = artistRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.artistMapper = artistMapper;
        this.storageService = storageService;
        this.userService = userService;
        this.favoritesService = favoritesService;
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO findById(Long id) {
        Optional<Artist> optionalArtist = this.artistRepository.findById(id);
        if (optionalArtist.isPresent()) {
            Artist artist = optionalArtist.get();
            ArtistDTO artistDTO = this.artistMapper.entityToDto(artist);
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(artist.getId(), this.storageType,
                    MediaRef.ARTIST_AVATAR, Status.ACTIVE);
            optionalResourceInfo.ifPresent(
                resourceInfo -> artistDTO.setAvatarUrl(this.storageService.getFullUrl(resourceInfo)));
            this.favoritesService.setLike(artistDTO, EntityType.ARTIST);
            return artistDTO;
        } else {
            throw new EntityNotFoundException("Artist not found!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO findByName(String name) {
        return this.artistMapper.entityToDto(artistRepository.findByName(name));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findByConditions(Pageable pageable, ArtistSearchDTO artistSearchDTO) {
        return this.artistRepository.findByConditions(pageable, artistSearchDTO);
    }

    @Override
    @Transactional
    public ArtistDTO create(ArtistDTO artist, MultipartFile multipartFile) {
        UserInfo currentUser = this.userService.getCurrentUserInfo();
        String unaccentName = StringAccentRemover.removeStringAccent(artist.getName());
        Artist artistToSave = this.artistMapper.dtoToEntity(artist);
        artistToSave.setLikeCount(0L);
        artistToSave.setUploader(currentUser);
        this.artistRepository.saveAndFlush(artistToSave);
        ResourceInfo resourceInfo = this.storageService.upload(multipartFile, artistToSave);
        artist.setId(artistToSave.getId());
        artist.setName(artistToSave.getName());
        artist.setUnaccentName(unaccentName.toLowerCase());
        artist.setAvatarUrl(this.storageService.getFullUrl(resourceInfo));
        return artist;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistDTO> findAll(Pageable pageable) {
        return this.artistRepository.findByConditions(pageable, new ArtistSearchDTO());
    }

    @Override
    @Transactional
    public ArtistDTO update(Long id, ArtistDTO artist, MultipartFile multipartFile)
        throws IOException {
        Optional<Artist> oldArtist = this.artistRepository.findById(id);
        if (oldArtist.isPresent()) {
            if (multipartFile != null) {
                ResourceInfo resourceInfo = this.storageService
                    .upload(multipartFile, oldArtist.get());
                oldArtist.get().setAvatarResource(resourceInfo);
                artist.setAvatarUrl(this.storageService.getFullUrl(resourceInfo));
            }
            oldArtist.get().setName(artist.getName());
            oldArtist.get()
                .setUnaccentName(StringAccentRemover.removeStringAccent(artist.getName()));
            return artist;
        } else {
            throw new RuntimeException("Artist does not exist");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }

    @Override
    public Map<Long, Boolean> getUserArtistLikeMap(Map<Long, Boolean> artistIdMap) {
        return null;
    }
}
