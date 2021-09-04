package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.mapper.ArtistMapper;
import com.alpha.mapper.ResourceInfoMapper;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.model.dto.ResourceInfoDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ArtistRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.ArtistService;
import com.alpha.service.StorageService;
import com.alpha.util.formatter.StringAccentRemover;
import java.io.IOException;
import java.util.Optional;
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

    private final ResourceInfoMapper resourceInfoMapper;

    private final StorageService storageService;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository,
        ResourceInfoRepository resourceInfoRepository,
        ArtistMapper artistMapper,
        ResourceInfoMapper resourceInfoMapper, StorageService storageService) {
        this.artistRepository = artistRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.artistMapper = artistMapper;
        this.resourceInfoMapper = resourceInfoMapper;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ArtistDTO> findById(Long id) {
        Optional<Artist> optionalArtist = this.artistRepository.findById(id);
        return optionalArtist.map(artist -> {
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(artist.getId(), this.storageType,
                    MediaRef.ARTIST_AVATAR, Status.ACTIVE);
            optionalResourceInfo.ifPresent(
                resourceInfo -> artist.setAvatarUrl(this.storageService.getFullUrl(resourceInfo)));
            return this.artistMapper.entityToDto(artist);
        });
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
    public ArtistDTO create(ArtistDTO artist,
        MultipartFile multipartFile) {
        try {
            String unaccentName = StringAccentRemover.removeStringAccent(artist.getName());
            Artist artistToSave = this.artistMapper.dtoToEntity(artist);
            this.artistRepository.saveAndFlush(artistToSave);
            ResourceInfo resourceInfo = this.storageService.upload(multipartFile, artistToSave);
            artist.setId(artistToSave.getId());
            artist.setName(artistToSave.getName());
            artist.setUnaccentName(unaccentName.toLowerCase());
            artist.setAvatarUrl(this.storageService.getFullUrl(resourceInfo));
            return artist;
        } catch (IOException ex) {
            log.error(ex);
            throw new RuntimeException(ex);
        }
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
                ResourceInfo oldResourceInfo = this.resourceInfoRepository
                    .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id, this.storageType,
                        MediaRef.ARTIST_AVATAR, Status.ACTIVE).orElse(null);
                ResourceInfo resourceInfo = this.storageService
                    .upload(multipartFile, oldArtist.get(), oldResourceInfo);
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
}
