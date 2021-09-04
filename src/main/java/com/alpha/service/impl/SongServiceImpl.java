package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.CommonConstants;
import com.alpha.constant.MediaRef;
import com.alpha.constant.Status;
import com.alpha.mapper.ArtistMapper;
import com.alpha.mapper.SongMapper;
import com.alpha.mapper.TagMapper;
import com.alpha.mapper.UserInfoMapper;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.UserFavoriteSong;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.AlbumRepository;
import com.alpha.repositories.LikeRepository;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.LikeService;
import com.alpha.service.LikeService.ListeningConfig;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    private final LikeRepository likeRepository;

    private final TagRepository tagRepository;

    private final AlbumRepository albumRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final StorageService storageService;

    private final SongMapper songMapper;

    private final ArtistMapper artistMapper;

    private final TagMapper tagMapper;

    private final UserInfoMapper userInfoMapper;

    private final UserService userService;

    @Value("${storage.storage-type}")
    private StorageType storageType;

    @Autowired
    public SongServiceImpl(SongRepository songRepository, AlbumRepository albumRepository,
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
        this.tagMapper = tagMapper;
        this.albumRepository = albumRepository;
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
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id, this.storageType, MediaRef.SONG_AUDIO, Status.ACTIVE);
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
    public Iterable<SongDTO> findAllByTitle(String title) {
        Spliterator<Song> songSpliterator = this.songRepository.findAllByTitle(title).spliterator();
        return StreamSupport.stream(songSpliterator, false)
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<SongDTO> findAllByTitleContaining(String name) {
        Spliterator<Song> songSpliterator;
        if (name.equals(StringAccentRemover.removeStringAccent(name))) {
            songSpliterator = this.songRepository
                    .findAllByUnaccentTitleContainingIgnoreCase(name).spliterator();
        } else {
            songSpliterator = this.songRepository
                    .findAllByTitleContainingIgnoreCase(name).spliterator();
        }
        return StreamSupport.stream(songSpliterator, false)
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByTitleContaining(String name, Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByTitleContaining(name, pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional
    public SongDTO uploadAndSaveSong(MultipartFile file, SongDTO songDTO)
        throws IOException {
        UserInfoDTO userInfoDTO = this.userService.getCurrentProfile();
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
    public SongDTO save(SongDTO song) {
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
    public void deleteById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            this.songRepository.deleteById(id);
            Optional<ResourceInfo> resourceInfoOptional = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(song.get().getId(), this.storageType,
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

    @Override
    public void setFields(SongDTO oldSongInfo, SongDTO newSongInfo) {
        oldSongInfo.setTitle(newSongInfo.getTitle());
        oldSongInfo.setArtists(newSongInfo.getArtists());
        oldSongInfo.setGenres(newSongInfo.getGenres());
        oldSongInfo.setCountry(newSongInfo.getCountry());
        oldSongInfo.setReleaseDate(newSongInfo.getReleaseDate());
        oldSongInfo.setTags(newSongInfo.getTags());
        oldSongInfo.setTheme(newSongInfo.getTheme());
        if (newSongInfo.getUrl() != null) {
            oldSongInfo.setUrl(newSongInfo.getUrl());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> sortByDate(Pageable pageable) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLiked(Long songId) {
        OAuth2AuthenticatedPrincipal oAuth2AuthenticatedPrincipal =
                this.userService.getCurrentUser();
        if (oAuth2AuthenticatedPrincipal == null) return false;
        String username = oAuth2AuthenticatedPrincipal.getName();
        Like like = this.likeRepository.findByLikeId_SongIdAndLikeId_Username(songId, username);
        return (like != null);
    }

    @Override
    @Transactional(readOnly = true)
    public void setLike(SongDTO song) {
        song.setLiked(hasUserLiked(song.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public void setLike(Page<SongDTO> songList) {
        for (SongDTO song : songList) {
            song.setLiked(hasUserLiked(song.getId()));
        }
    }

    @Override
    public void setLike(Iterable<SongDTO> songList) {
        for (SongDTO song : songList) {
            song.setLiked(hasUserLiked(song.getId()));
        }
    }

    @Override
    @Transactional
    public void uploadAndSaveSong(MultipartFile file, SongDTO songDTO, Long albumId) throws IOException {
        Song songToSave = this.songRepository.save(this.songMapper.dtoToEntity(songDTO));
        String fileDownloadUri = this.storageService.upload(file, songToSave);
        songToSave.setUrl(fileDownloadUri);
        Map<String, Object> userShortInfo = this.userService.getCurrentUserShortInfo();
        UserInfo userInfo = UserInfoJsonStringifier.stringify(userShortInfo);
        songToSave.setUploader(userInfo);
        if (albumId != null) {
            Optional<Album> album = this.albumRepository.findById(albumId);
            if (album.isPresent()) {
                Collection<Song> songList = album.get().getSongs();
                if (songList == null) {
                    songList = new ArrayList<>();
                }
                songList.add(songToSave);
                album.get().setSongs(songList);
                this.albumRepository.save(album.get());
            }
        }
        this.songRepository.save(songToSave);
    }
}
