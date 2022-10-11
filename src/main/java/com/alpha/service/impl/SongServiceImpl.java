package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.EntityType;
import com.alpha.constant.MediaRef;
import com.alpha.constant.ModelStatus;
import com.alpha.constant.RoleConstants;
import com.alpha.elastic.model.SongEs;
import com.alpha.elastic.repo.SongEsRepository;
import com.alpha.mapper.ArtistMapper;
import com.alpha.mapper.SongMapper;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongDTO.SongAdditionalInfoDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.UserInfo;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.repositories.TagRepository;
import com.alpha.service.FavoritesService;
import com.alpha.service.SongService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import java.util.ArrayList;
import java.util.Collection;
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

    private final SongEsRepository songEsRepository;

    private final TagRepository tagRepository;

    private final ResourceInfoRepository resourceInfoRepository;

    private final StorageService storageService;

    private final SongMapper songMapper;

    private final ArtistMapper artistMapper;

    private final UserService userService;

    private final FavoritesService favoritesService;

    @Value("${storage.storage-type}")
    private StorageType storageType;

    @Autowired
    public SongServiceImpl(SongRepository songRepository,
        SongEsRepository songEsRepository, UserService userService,
        TagRepository tagRepository, ResourceInfoRepository resourceInfoRepository,
        StorageService storageService, SongMapper songMapper,
        ArtistMapper artistMapper, FavoritesService favoritesService) {
        this.songRepository = songRepository;
        this.songEsRepository = songEsRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.resourceInfoRepository = resourceInfoRepository;
        this.storageService = storageService;
        this.songMapper = songMapper;
        this.artistMapper = artistMapper;
        this.favoritesService = favoritesService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAll(Pageable pageable) {
        Page<SongDTO> songDTOPage = this.songRepository
            .findAllConditions(pageable, new SongSearchDTO());
        this.favoritesService.setLikes(songDTOPage, EntityType.SONG);
        return songDTOPage;
    }

    @Override
    @SneakyThrows
    @Transactional(readOnly = true)
    public Page<SongEs> findPageByName(String name, Pageable pageable) {
        String phrase = StringAccentRemover.removeStringAccent(name);
        log.info("Phrase {}", phrase);
//        return this.songEsRepository.findAll(pageable);
        return this.songEsRepository.findPageByName(name, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByConditions(Pageable pageable, SongSearchDTO songSearchDTO) {
        Page<SongDTO> songDTOPage = this.songRepository
            .findAllConditions(pageable, songSearchDTO);
        this.favoritesService.setLikes(songDTOPage, EntityType.SONG);
        return songDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public SongDTO findById(Long id) {
        Optional<Song> optionalSong = this.songRepository.findById(id);
        if (optionalSong.isPresent()) {
            Optional<ResourceInfo> optionalResourceInfo = this.resourceInfoRepository
                .findByMediaIdAndStorageTypeAndMediaRefAndStatus(id, this.storageType,
                    MediaRef.SONG_AUDIO, ModelStatus.ACTIVE);
            SongDTO songDTO = this.songMapper.entityToDto(optionalSong.get());
            this.favoritesService.setLike(songDTO, EntityType.SONG);
            optionalResourceInfo.ifPresent(
                resourceInfo -> songDTO.setUrl(resourceInfo.getUri()));
            return songDTO;
        } else {
            throw new EntityNotFoundException("Không tìm thấy bài hát");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SongAdditionalInfoDTO findAdditionalInfoById(Long id) {
        return this.songRepository.findAdditionalInfo(id);
    }

    @Override
    @Transactional
    public SongDTO upload(MultipartFile file, SongDTO songDTO) {
        UserInfo userInfo = this.userService.getCurrentUserInfo();
        Song song = Song.builder()
            .listeningFrequency(0L)
            .likeCount(0L)
            .uploader(userInfo)
            .createTime(new Date())
            .status(ModelStatus.ACTIVE)
            .sync(0)
            .build();
        this.patchSongUploadToEntity(songDTO, song);
        this.songRepository.saveAndFlush(song);
        ResourceInfo resourceInfo = this.storageService.upload(file, song);
        songDTO.setUrl(resourceInfo.getUri());
        songDTO.setUnaccentTitle(song.getUnaccentTitle());
        songDTO.setId(song.getId());
        return songDTO;
    }

    @Override
    @Transactional
    public SongDTO update(Long id, SongDTO songDTO, MultipartFile file) {
        boolean isAdmin = this.userService.hasAuthority(RoleConstants.SONG_MANAGEMENT);
        Optional<Song> oldSong;
        if (isAdmin) {
            oldSong = this.songRepository.findById(id);
        } else {
            String username = this.userService.getCurrentUsername();
            oldSong = this.songRepository.findByIdAndUploader_Username(id, username);
        }
        if (oldSong.isPresent()) {
            Song song = oldSong.get();
            if (file != null) {
                ResourceInfo resourceInfo = this.storageService
                    .upload(file, song);
                song.setAudioResource(resourceInfo);
                songDTO.setUrl(resourceInfo.getUri());
            }
            this.patchSongUploadToEntity(songDTO, song);
            song.setUpdateTime(new Date());
            song.setSync(0);
            this.songRepository.save(song);
            songDTO.setUnaccentTitle(song.getUnaccentTitle());
            return songDTO;
        } else {
            throw new EntityNotFoundException("Song does not existed or user is not the owner");
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
                    MediaRef.SONG_AUDIO, ModelStatus.ACTIVE);
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

    private void patchSongUploadToEntity(SongDTO songDTO, Song song) {
        song.setId(songDTO.getId());
        song.setTitle(songDTO.getTitle());
        song.setUnaccentTitle(StringAccentRemover.removeStringAccent(songDTO.getTitle()));
        song.setReleaseDate(songDTO.getReleaseDate());
        song.setDuration(songDTO.getDuration());
        List<Artist> artistList = this.artistMapper
            .dtoToEntityListPure(new ArrayList<>(songDTO.getArtists()));
        song.setArtists(artistList);
        SongAdditionalInfoDTO songAdditionalInfoDTO = songDTO.getAdditionalInfo();
        if (songAdditionalInfoDTO != null) {
            Song additionalInfoSong = this.songMapper.dtoToEntityAdditional(songAdditionalInfoDTO);
            song.setCountry(additionalInfoSong.getCountry());
            song.setLyric(additionalInfoSong.getLyric());
            song.setGenres(additionalInfoSong.getGenres());
            Map<String, Boolean> tagMap = songAdditionalInfoDTO.getTags()
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
            song.setTags(mergedTagList);
            song.setTheme(additionalInfoSong.getTheme());
        }
        song.setSync(0);
    }
}
