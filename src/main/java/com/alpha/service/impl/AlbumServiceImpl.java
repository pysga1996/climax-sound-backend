package com.alpha.service.impl;

import com.alpha.constant.EntityType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.ModelStatus;
import com.alpha.constant.RoleConstants;
import com.alpha.elastic.model.AlbumEs;
import com.alpha.elastic.repo.AlbumEsRepository;
import com.alpha.mapper.AlbumMapper;
import com.alpha.mapper.ArtistMapper;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.AlbumService;
import com.alpha.service.FavoritesService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final AlbumEsRepository albumEsRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final TagRepository tagRepository;

    private final AlbumMapper albumMapper;

    private final ArtistMapper artistMapper;

    private final UserService userService;

    private final StorageService storageService;

    private final FavoritesService favoritesService;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository,
        AlbumEsRepository albumEsRepository,
        ResourceInfoRepository resourceInfoRepository,
        TagRepository tagRepository, AlbumMapper albumMapper,
        ArtistMapper artistMapper, UserService userService,
        StorageService storageService, FavoritesService favoritesService) {
        this.albumRepository = albumRepository;
        this.albumEsRepository = albumEsRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.tagRepository = tagRepository;
        this.albumMapper = albumMapper;
        this.artistMapper = artistMapper;
        this.userService = userService;
        this.storageService = storageService;
        this.favoritesService = favoritesService;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDTO detail(Long id, Pageable pageable) {
        Optional<Album> albumOptional = this.albumRepository.findById(id);
        if (albumOptional.isPresent()) {
            Optional<ResourceInfo> oldResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id,
                    this.storageService.getStorageType(), MediaRef.ALBUM_COVER, ModelStatus.ACTIVE);
            AlbumDTO albumDTO = this.albumMapper.entityToDtoPure(albumOptional.get());
            oldResourceInfo.ifPresent(
                resourceInfo -> albumDTO.setCoverUrl(resourceInfo.getUri()));
            this.favoritesService.setLike(albumDTO, EntityType.ALBUM);
            return albumDTO;
        } else {
            throw new EntityNotFoundException("Album not found");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumAdditionalInfoDTO findAdditionalInfoById(Long id) {
        return this.albumRepository.findAdditionalInfo(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDTO> findAll(Pageable pageable) {
        return this.albumRepository.findAllByConditions(pageable, new AlbumSearchDTO());
    }

    @Override
    @SneakyThrows
    @Transactional(readOnly = true)
    public Page<AlbumEs> findPageByName(String name, Pageable pageable) {
        String phrase = StringAccentRemover.removeStringAccent(name);
        log.info("Phrase {}", phrase);
//        return this.albumEsRepository.findAll(pageable)
//            .map(e -> {
//                e.setCoverUrl(this.storageService.getFullUrl(e.getResourceMap()));
//                return e;
//            });
        return this.albumEsRepository.findPageByName(name, pageable)
            .map(e -> {
                e.setCoverUrl(this.storageService.getFullUrl(e.getResourceMap()));
                return e;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumDTO> findAllByConditions(Pageable pageable,
        AlbumSearchDTO albumSearchDTO) {
        return this.albumRepository.findAllByConditions(pageable, albumSearchDTO);
    }

    @Override
    @Transactional
    public void create(AlbumDTO album) {
        this.albumRepository.saveAndFlush(this.albumMapper.dtoToEntity(album));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        this.albumRepository.deleteByIdProc(id);
    }

    @Override
    @Transactional
    public AlbumDTO upload(MultipartFile file, AlbumDTO albumDTO) {
        UserInfo userInfo = this.userService.getCurrentUserInfo();
        Album album = Album.builder()
            .listeningFrequency(0L)
            .likeCount(0L)
            .uploader(userInfo)
            .createTime(new Date())
            .status(ModelStatus.ACTIVE)
            .sync(0)
            .build();
        this.patchAlbumUploadToEntity(albumDTO, album);
        this.albumRepository.saveAndFlush(album);
        if (file != null) {
            ResourceInfo resourceInfo = this.storageService.upload(file, album);
            album.setCoverResource(resourceInfo);
            albumDTO.setCoverUrl(resourceInfo.getUri());
        }
        albumDTO.setId(album.getId());
        albumDTO.setUnaccentTitle(album.getUnaccentTitle());
        return albumDTO;
    }

    @Override
    @Transactional
    public AlbumDTO update(MultipartFile file, AlbumDTO albumDTO, Long id) {
        boolean isAdmin = this.userService.hasAuthority(RoleConstants.SONG_MANAGEMENT);
        Optional<Album> oldAlbum;
        if (isAdmin) {
            oldAlbum = this.albumRepository.findById(id);
        } else {
            String username = this.userService.getCurrentUsername();
            oldAlbum = this.albumRepository.findByIdAndUploader_Username(id, username);
        }
        if (oldAlbum.isPresent()) {
            Album album = oldAlbum.get();
            if (file != null) {
                ResourceInfo resourceInfo = this.storageService
                    .upload(file, album);
                album.setCoverResource(resourceInfo);
                albumDTO.setCoverUrl(resourceInfo.getUri());
            }
            this.patchAlbumUploadToEntity(albumDTO, album);
            album.setUpdateTime(new Date());
            album.setSync(0);
            this.albumRepository.save(album);
            albumDTO.setUnaccentTitle(album.getUnaccentTitle());
            return albumDTO;
        } else {
            throw new EntityNotFoundException("Album not existed or user is not the owner");
        }
    }

    @Override
    @Transactional
    public void updateSongList(Long albumId, List<AlbumUpdateDTO> songDTOList) {
        Optional<Album> album = this.albumRepository.findById(albumId);
        album.ifPresent(value -> this.albumRepository.updateSongList(albumId, songDTOList));
    }

    private void patchAlbumUploadToEntity(AlbumDTO albumDTO, Album album) {
        album.setId(albumDTO.getId());
        album.setTitle(albumDTO.getTitle());
        album.setUnaccentTitle(StringAccentRemover.removeStringAccent(albumDTO.getTitle()));
        album.setReleaseDate(albumDTO.getReleaseDate());
        album.setDuration(albumDTO.getDuration());
        List<Artist> artistList = this.artistMapper
            .dtoToEntityListPure(new ArrayList<>(albumDTO.getArtists()));
        album.setArtists(artistList);
        AlbumAdditionalInfoDTO albumAdditionalInfoDTO = albumDTO.getAdditionalInfo();
        if (albumAdditionalInfoDTO != null) {
            Album additionalInfoAlbum = this.albumMapper
                .dtoToEntityAdditional(albumAdditionalInfoDTO);
            album.setCountry(additionalInfoAlbum.getCountry());
            album.setDescription(additionalInfoAlbum.getDescription());
            album.setGenres(additionalInfoAlbum.getGenres());
            Map<String, Boolean> tagMap = albumAdditionalInfoDTO.getTags()
                .stream()
                .collect(Collectors.toMap(TagDTO::getName, e -> false));
            Set<String> tagNames = tagMap.keySet();
            List<Tag> existedTagList = this.tagRepository
                .findAllByNameInAndStatus(tagNames, ModelStatus.ACTIVE);
            existedTagList.forEach(tag -> tagMap.put(tag.getName(), true));
            List<Tag> nonExistedTagList = tagMap
                .entrySet()
                .stream()
                .filter(e -> !e.getValue())
                .map(e -> Tag.builder().name(e.getKey()).createTime(new Date())
                    .status(ModelStatus.ACTIVE).build())
                .collect(Collectors.toList());
            this.tagRepository.saveAll(nonExistedTagList);
            List<Tag> mergedTagList = new ArrayList<>(existedTagList);
            mergedTagList.addAll(nonExistedTagList);
            album.setTags(mergedTagList);
            album.setTheme(additionalInfoAlbum.getTheme());
        }
    }
}
