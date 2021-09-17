package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.RoleConstants;
import com.alpha.constant.MediaRef;
import com.alpha.constant.SchedulerConstants.ListeningConfig;
import com.alpha.constant.Status;
import com.alpha.mapper.ArtistMapper;
import com.alpha.mapper.SongMapper;
import com.alpha.mapper.TagMapper;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.UserFavoriteSong;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.LikeRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.LikeService;
import com.alpha.service.SongService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
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
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final LikeRepository likeRepository;

    private final TagRepository tagRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final StorageService storageService;

    private final SongMapper songMapper;

    private final ArtistMapper artistMapper;

    private final TagMapper tagMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserService userService;

    private final LikeService likeService;

    @Value("${storage.storage-type}")
    private StorageType storageType;

    @Autowired
    public SongServiceImpl(SongRepository songRepository,
        LikeRepository likeRepository, UserService userService,
        TagRepository tagRepository, ResourceInfoRepository resourceInfoRepository,
        StorageService storageService, SongMapper songMapper,
        ArtistMapper artistMapper, TagMapper tagMapper,
        UserInfoMapper userInfoMapper, LikeService likeService) {
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.storageService = storageService;
        this.songMapper = songMapper;
        this.artistMapper = artistMapper;
        this.tagMapper = tagMapper;
        this.userInfoMapper = userInfoMapper;
        this.likeService = likeService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAll(Pageable pageable) {
        Page<SongDTO> songDTOPage = this.songRepository
            .findAllConditions(pageable, new SongSearchDTO());
        this.setLikes(songDTOPage);
        return songDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByConditions(Pageable pageable, SongSearchDTO songSearchDTO) {
        Page<SongDTO> songDTOPage = this.songRepository
            .findAllConditions(pageable, songSearchDTO);
        this.setLikes(songDTOPage);
        return songDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public SongDTO findById(Long id) {
        Optional<Song> optionalSong = this.songRepository.findById(id);
        if (optionalSong.isPresent()) {
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id, this.storageType,
                    MediaRef.SONG_AUDIO, Status.ACTIVE);
            SongDTO songDTO = this.songMapper.entityToDto(optionalSong.get());
            this.setLike(Optional.of(songDTO));
            optionalResourceInfo.ifPresent(
                resourceInfo -> songDTO.setUrl(this.storageService.getFullUrl(resourceInfo)));
            return songDTO;
        } else {
            throw new EntityNotFoundException("Không tìm thấy bài hát");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void listenToSong(Long id) {
        if (this.userService.isAuthenticated()) {
            this.likeService
                .listen(id, ListeningConfig.SONG, this.userService.getCurrentUsername());
        } else {
            this.likeService.listen(id, ListeningConfig.SONG, null);
        }

    }

    @Override
    @Transactional
    public SongDTO uploadAndSaveSong(MultipartFile file, SongDTO songDTO)
        throws IOException {
        UserInfoDTO userInfoDTO = this.userService.getCurrentUserInfo();
        UserInfo userInfo = this.userInfoMapper.dtoToEntity(userInfoDTO);
        Song songToSave = this.songMapper.dtoToEntity(songDTO);
        songToSave.setUploader(userInfo);
        List<String> tagNames = songToSave.getTags()
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
        List<Tag> existedTagList = this.tagRepository.findAllByNameIn(tagNames);
        List<String> existedTagNames = existedTagList
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());
        List<Tag> nonExistedTagList = songToSave.getTags()
            .stream()
            .filter(e -> !existedTagNames.contains(e.getName()))
            .collect(Collectors.toList());
        this.tagRepository.saveAll(nonExistedTagList);
        List<Tag> mergedTagList = new ArrayList<>(existedTagList);
        mergedTagList.addAll(nonExistedTagList);
        songToSave.setTags(mergedTagList);
        this.songRepository.saveAndFlush(songToSave);
        ResourceInfo resourceInfo = this.storageService.upload(file, songToSave);
        songDTO.setUrl(this.storageService.getFullUrl(resourceInfo));
        songDTO.setId(songToSave.getId());
        return songDTO;
    }

    @Override
    @Transactional
    public SongDTO create(SongDTO song) {
        if (song.getTags() != null) {
            Collection<TagDTO> tags = song.getTags();
            for (TagDTO tag : tags) {
                if (tagRepository.findByName("tag") == null) {
                    this.tagRepository.saveAndFlush(this.tagMapper.dtoToEntity(tag));
                }
            }
        }
        String unaccentTitle = StringAccentRemover.removeStringAccent(song.getTitle());
        song.setUnaccentTitle(unaccentTitle.toLowerCase());
        Song songEntity = this.songMapper.dtoToEntity(song);
        this.songRepository.saveAndFlush(songEntity);
        song.setId(songEntity.getId());
        return song;
    }

    @Override
    @Transactional
    public SongDTO update(Long id, SongDTO songDTO, MultipartFile multipartFile)
        throws IOException {
        boolean isAdmin = this.userService.hasAuthority(RoleConstants.SONG_MANAGEMENT);
        Optional<Song> oldSong;
        if (isAdmin) {
            oldSong = this.songRepository.findById(id);
        } else {
            String username = this.userService.getCurrentUsername();
            oldSong = this.songRepository.findByIdAndUploader_Username(id, username);
        }
        if (oldSong.isPresent()) {
            if (multipartFile != null) {
                ResourceInfo resourceInfo = this.storageService
                    .upload(multipartFile, oldSong.get(), oldSong.get().getAudioResource());
                oldSong.get().setAudioResource(resourceInfo);
                songDTO.setUrl(this.storageService.getFullUrl(resourceInfo));
            }
            oldSong.get().setTitle(songDTO.getTitle());
            oldSong.get()
                .setUnaccentTitle(StringAccentRemover.removeStringAccent(songDTO.getTitle()));
            oldSong.get().setReleaseDate(songDTO.getReleaseDate());
            oldSong.get().setArtists(this.artistMapper.dtoToEntityListPure(
                new ArrayList<>(songDTO.getArtists())));
            return songDTO;
        } else {
            throw new EntityNotFoundException("Song does not existed or user is not the uploader");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            this.songRepository.deleteById(id);
            Optional<ResourceInfo> resourceInfoOptional = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(song.get().getId(),
                    this.storageType,
                    MediaRef.SONG_AUDIO, Status.ACTIVE);
            resourceInfoOptional.ifPresent(this.storageService::delete);
        }
    }

    @Override
    @Transactional
    public void deleteAll(Collection<SongDTO> songs) {
        this.songRepository.deleteInBatch(songs.stream()
            .map(this.songMapper::dtoToEntity)
            .collect(Collectors.toList()));
    }

    private void setLike(Optional<SongDTO> optionalSongDTO) {
        if (!optionalSongDTO.isPresent()) {
            return;
        }
        String currentUsername = this.userService.getCurrentUsername();
        AtomicLong noCachedId = new AtomicLong(-1);
        Long songId = optionalSongDTO.get().getId();
        boolean isLiked = this.likeRepository
            .getUserSongLikeFromCache(currentUsername, songId, noCachedId);
        if (noCachedId.get() >= 0) {
            UserFavoriteSong userFavoriteSong = this.likeRepository
                .getUserSongLike(currentUsername, songId);
            if (userFavoriteSong != null) {
                isLiked = this.likeRepository
                    .setUserSongLikeToCache(currentUsername, songId, userFavoriteSong.isLiked());
            }
        }
        optionalSongDTO.get().setLiked(isLiked);
    }

    private void setLikes(Page<SongDTO> songDTOPage) {
        Map<Long, SongDTO> songDTOMap = songDTOPage
            .stream()
            .collect(Collectors.toMap(SongDTO::getId, e -> e));
        String currentUsername = this.userService.getCurrentUsername();
        List<Long> noCachedSongIds = new ArrayList<>();
        for (Entry<Long, SongDTO> entry : songDTOMap.entrySet()) {
            boolean isLiked = this.likeRepository
                .getUserSongLikeFromCache(currentUsername, entry.getKey(), noCachedSongIds);
            entry.getValue().setLiked(isLiked);
        }
        if (noCachedSongIds.size() > 0) {
            boolean isLiked;
            Long songId;
            List<UserFavoriteSong> userFavoriteSongList = this.likeRepository
                .getUserSongLikeMap(currentUsername, noCachedSongIds);
            for (UserFavoriteSong userFavoriteSong : userFavoriteSongList) {
                songId = userFavoriteSong.getUserFavoriteSongId().getSongId();
                isLiked = this.likeRepository
                    .setUserSongLikeToCache(currentUsername, songId, userFavoriteSong.isLiked());
                songDTOMap.get(songId).setLiked(isLiked);
            }
        }
    }

    public Map<Long, Boolean> getUserSongLikeMap(Map<Long, Boolean> songIdMap) {
        String currentUsername = this.userService.getCurrentUsername();
        List<Long> songIds = new ArrayList<>(songIdMap.keySet());
        List<Long> noCachedSongIds = new ArrayList<>();
        for (Long songId : songIds) {
            boolean isLiked = this.likeRepository
                .getUserSongLikeFromCache(currentUsername, songId, noCachedSongIds);
            songIdMap.replace(songId, isLiked);
        }
        List<UserFavoriteSong> userFavoriteSongList = this.likeRepository
            .getUserSongLikeMap(currentUsername, noCachedSongIds);
        userFavoriteSongList
            .forEach(e -> songIdMap.replace(e.getUserFavoriteSongId().getSongId(), e.isLiked()));
        return songIdMap;
    }
}
