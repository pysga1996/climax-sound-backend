package com.alpha.service.impl;

import com.alpha.constant.MediaRef;
import com.alpha.constant.RoleConstants;
import com.alpha.constant.Status;
import com.alpha.mapper.AlbumMapper;
import com.alpha.mapper.ArtistMapper;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.AlbumService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final TagRepository tagRepository;

    private final AlbumMapper albumMapper;

    private final ArtistMapper artistMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserService userService;

    private final StorageService storageService;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository,
        ResourceInfoRepository resourceInfoRepository,
        TagRepository tagRepository, AlbumMapper albumMapper,
        ArtistMapper artistMapper, UserInfoMapper userInfoMapper,
        UserService userService, StorageService storageService) {
        this.albumRepository = albumRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.tagRepository = tagRepository;
        this.albumMapper = albumMapper;
        this.artistMapper = artistMapper;
        this.userInfoMapper = userInfoMapper;
        this.userService = userService;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumDTO detail(Long id, Pageable pageable) {
        Optional<Album> albumOptional = this.albumRepository.findById(id);
        if (albumOptional.isPresent()) {
            Optional<ResourceInfo> oldResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id,
                    this.storageService.getStorageType(), MediaRef.ALBUM_COVER, Status.ACTIVE);
            AlbumDTO albumDTO = this.albumMapper.entityToDtoPure(albumOptional.get());
            oldResourceInfo.ifPresent(
                resourceInfo -> albumDTO.setCoverUrl(this.storageService.getFullUrl(resourceInfo)));
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
    public AlbumDTO uploadAndSaveAlbum(MultipartFile file, AlbumDTO albumDTO) {
        Album album = new Album();
        this.patchAlbumUploadToEntity(albumDTO, album);
        UserInfoDTO userInfoDTO = this.userService.getCurrentUserInfoDTO();
        UserInfo userInfo = this.userInfoMapper.dtoToEntity(userInfoDTO);
        album.setUploader(userInfo);
        this.albumRepository.saveAndFlush(album);
        albumDTO.setId(album.getId());
        if (file != null) {
            ResourceInfo resourceInfo = this.storageService.upload(file, album);
            albumDTO.setCoverUrl(this.storageService.getFullUrl(resourceInfo));
        }
        return albumDTO;
    }

    @Override
    @Transactional
    public void updateSongList(Long albumId, List<AlbumUpdateDTO> songDTOList) {
        Optional<Album> album = this.albumRepository.findById(albumId);
        album.ifPresent(value -> this.albumRepository.updateSongList(albumId, songDTOList));
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
                ResourceInfo oldResourceInfo = this.resourceInfoRepository
                    .findByMediaIdAndStorageTypeAndMediaRefAndStatus(album.getId(),
                        this.storageService.getStorageType(), MediaRef.ALBUM_COVER, Status.ACTIVE)
                    .orElse(null);
                ResourceInfo resourceInfo = this.storageService
                    .upload(file, album, oldResourceInfo);
                album.setCoverResource(resourceInfo);
                albumDTO.setCoverUrl(this.storageService.getFullUrl(resourceInfo));
            }
            this.patchAlbumUploadToEntity(albumDTO, album);
            this.albumRepository.save(album);
            return albumDTO;
        } else {
            throw new EntityNotFoundException("Album not existed or user is not the owner");
        }
    }

    @Override
    public AlbumDTO listenToAlbum(Long albumId) {
        // TODO add listening queue
//        Optional<AlbumDTO> optionalAlbumDTO = this.albumRepository.findMediaListNative(albumId);
//        return optionalAlbumDTO
//            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy album"));
        return null;
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
        AlbumAdditionalInfoDTO adlbumAdditionalInfoDTO = albumDTO.getAdditionalInfo();
        if (adlbumAdditionalInfoDTO != null) {
            Album additionalInfoAlbum = this.albumMapper
                .dtoToEntityAdditional(adlbumAdditionalInfoDTO);
            album.setCountry(additionalInfoAlbum.getCountry());
            album.setDescription(additionalInfoAlbum.getDescription());
            album.setGenres(additionalInfoAlbum.getGenres());
            Map<String, Boolean> tagMap = adlbumAdditionalInfoDTO.getTags()
                .stream()
                .collect(Collectors.toMap(TagDTO::getName, e -> false));
            Set<String> tagNames = tagMap.keySet();
            List<Tag> existedTagList = this.tagRepository.findAllByNameIn(tagNames);
            existedTagList.forEach(tag -> tagMap.put(tag.getName(), true));
            List<Tag> nonExistedTagList = tagMap
                .entrySet()
                .stream()
                .filter(e -> !e.getValue())
                .map(e -> Tag.builder().name(e.getKey()).build())
                .collect(Collectors.toList());
            this.tagRepository.saveAll(nonExistedTagList);
            List<Tag> mergedTagList = new ArrayList<>(existedTagList);
            mergedTagList.addAll(nonExistedTagList);
            album.setTags(mergedTagList);
            album.setTheme(additionalInfoAlbum.getTheme());
        }
    }
}
