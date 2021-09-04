package com.alpha.service.impl;

import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.mapper.AlbumMapper;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumSearchDTO;
import com.alpha.model.dto.AlbumUpdateDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.AlbumService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    private final AlbumMapper albumMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserService userService;

    private final StorageService storageService;

    @Autowired
    public AlbumServiceImpl(AlbumRepository albumRepository,
        ResourceInfoRepository resourceInfoRepository,
        AlbumMapper albumMapper, UserInfoMapper userInfoMapper,
        UserService userService, StorageService storageService) {
        this.albumRepository = albumRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.albumMapper = albumMapper;
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
            AlbumDTO albumDTO = this.albumMapper.entityToDto(albumOptional.get());
            oldResourceInfo.ifPresent(
                resourceInfo -> albumDTO.setCoverUrl(this.storageService.getFullUrl(resourceInfo)));
            return albumDTO;
        } else {
            throw new EntityNotFoundException("Album not found");
        }
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
    public AlbumDTO uploadAndSaveAlbum(MultipartFile file, AlbumDTO album) throws IOException {
        Album albumToSave = this.albumMapper.dtoToEntity(album);
        UserInfoDTO userInfoDTO = this.userService.getCurrentProfile();
        UserInfo userInfo = this.userInfoMapper.dtoToEntity(userInfoDTO);
        albumToSave.setUploader(userInfo);
        this.albumRepository.saveAndFlush(albumToSave);
        album.setId(albumToSave.getId());
        if (file != null) {
            ResourceInfo resourceInfo = this.storageService.upload(file, albumToSave);
            album.setCoverUrl(this.storageService.getFullUrl(resourceInfo));
        }
        return album;
    }

    @Override
    @Transactional
    public void updateSongList(Long albumId, List<AlbumUpdateDTO> songDTOList) {
        Optional<Album> album = this.albumRepository.findById(albumId);
        album.ifPresent(value -> this.albumRepository.updateSongList(albumId, songDTOList));
    }

    @Override
    @Transactional
    public AlbumDTO edit(MultipartFile file, AlbumDTO albumDTO, Long id) throws IOException {
        Optional<Album> oldAlbum = this.albumRepository.findById(id);
        if (oldAlbum.isPresent()) {
            if (file != null) {
                ResourceInfo oldResourceInfo = this.resourceInfoRepository
                    .findByMediaIdAndStorageTypeAndMediaRefAndStatus(oldAlbum.get().getId(),
                        this.storageService.getStorageType(), MediaRef.ALBUM_COVER, Status.ACTIVE)
                    .orElse(null);
                ResourceInfo resourceInfo = this.storageService
                    .upload(file, oldAlbum.get(), oldResourceInfo);
                oldAlbum.get().setCoverResource(resourceInfo);
                albumDTO.setCoverUrl(this.storageService.getFullUrl(resourceInfo));
            }
            albumDTO.setId(id);
            Album albumToSave = this.albumMapper.dtoToEntity(albumDTO);
            oldAlbum.get().setCountry(albumToSave.getCountry());
            oldAlbum.get().setArtists(albumToSave.getArtists());
            oldAlbum.get().setGenres(albumToSave.getGenres());
            oldAlbum.get().setReleaseDate(albumToSave.getReleaseDate());
            oldAlbum.get().setTags(albumToSave.getTags());
            oldAlbum.get().setTitle(albumToSave.getTitle());
            oldAlbum.get()
                .setUnaccentTitle(StringAccentRemover.removeStringAccent(albumToSave.getTitle()));
            return albumDTO;
        } else {
            throw new EntityNotFoundException("Album not found");
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
}
