package com.alpha.service.impl;

import com.alpha.mapper.SongMapper;
import com.alpha.mapper.TagMapper;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Like;
import com.alpha.model.entity.Song;
import com.alpha.repositories.*;
import com.alpha.service.SongService;
import com.alpha.service.StorageService;
import com.alpha.service.UserService;
import com.alpha.util.formatter.StringAccentRemover;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class SongServiceImpl implements SongService {

    private final ArtistRepository artistRepository;

    private final SongRepository songRepository;

    private final LikeRepository likeRepository;

    private final UserService userService;

    private final TagRepository tagRepository;

    private final StorageService storageService;

    private final SongMapper songMapper;

    private final TagMapper tagMapper;

    private final AlbumRepository albumRepository;

    @Autowired
    public SongServiceImpl(ArtistRepository artistRepository, SongRepository songRepository,
                           AlbumRepository albumRepository,
                           LikeRepository likeRepository, UserService userService,
                           TagRepository tagRepository, StorageService storageService,
                           SongMapper songMapper, TagMapper tagMapper) {
        this.artistRepository = artistRepository;
        this.songRepository = songRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.storageService = storageService;
        this.songMapper = songMapper;
        this.tagMapper = tagMapper;
        this.albumRepository = albumRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAll(Pageable pageable, String sort) {
        Page<Song> songPage;
        if (sort != null && sort.equals("releaseDate")) {
            songPage = this.songRepository.findAllByOrderByReleaseDateDesc(pageable);
        } else if (sort != null && sort.equals("listeningFrequency")) {
            songPage = this.songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
        } else if (sort != null && sort.equals("likesCount")) {
//            return songRepository.findAllByOrderByUsers_Size(pageable);
            songPage = new PageImpl<>(new ArrayList<>());
        } else {
            songPage = this.songRepository.findAll(pageable);
        }
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<SongDTO> findAll() {
        return this.songRepository.findAll().stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<SongDTO> findTop10By(String sort) {
        return StreamSupport.stream(
                this.songRepository.findFirst10ByOrderByListeningFrequencyDesc().spliterator(), false)
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByOrderByReleaseDateDesc(Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByOrderByReleaseDateDesc(pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByOrderByDisplayRatingDesc(Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByOrderByDisplayRatingDesc(pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByOrderByListeningFrequencyDesc(Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByLikesCount(Pageable pageable) {
//        return songRepository.findAllByOrderByUsers_Size(pageable);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByUploader_Id(Long id, Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByUploader_Id(id, pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SongDTO> findById(Long id) {
        return this.songRepository.findById(id).map(this.songMapper::entityToDto);
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
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByArtistsContains(ArtistDTO artist, Pageable pageable) {
        Optional<Artist> artistOptional = this.artistRepository.findById(artist.getId());
        if (artistOptional.isPresent()) {
            Page<Song> songPage = this.songRepository
                    .findAllByArtistsContains(artistOptional.get(), pageable);
            return new PageImpl<>(songPage.getContent()
                    .stream()
                    .map(this.songMapper::entityToDto)
                    .collect(Collectors.toList()), pageable, songPage.getTotalElements());
        } else return new PageImpl<>(new ArrayList<>());

    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByUsersContains(UserDTO user, Pageable pageable) {
        Page<Song> songList = this.songRepository.findAllByUsersContains(user, pageable);
        Page<SongDTO> songDTOPage = songList.map(this.songMapper::entityToDto);
        setLike(songDTOPage);
        return songDTOPage;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SongDTO> findAllByTag_Name(String name, Pageable pageable) {
        Page<Song> songPage = this.songRepository.findAllByTag_Name(name, pageable);
        return new PageImpl<>(songPage.getContent()
                .stream()
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList()), pageable, songPage.getTotalElements());

    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<SongDTO> findAllByAlbum_Id(Long id) {
        Spliterator<Song> songSpliterator = this.songRepository.findAllByAlbum_Id(id).spliterator();
        return StreamSupport.stream(songSpliterator, false)
                .map(this.songMapper::entityToDto)
                .collect(Collectors.toList());
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
//            String fileUrl = song.get().getUrl();
//            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//            return audioStorageService.deleteLocalStorageFile(audioStorageService.audioStorageLocation, filename);
            this.storageService.delete(song.get());
        }
    }

    @Override
    @Transactional
    public void deleteAll(Collection<SongDTO> songs) {
        this.songRepository.deleteAll(songs.stream()
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
        UserDTO userDTO = this.userService.getCurrentUser();
        if (userDTO == null) return false;
        Long userId = userDTO.getId();
        Like like = this.likeRepository.findByLikeId_SongIdAndLikeId_UserId(songId, userId);
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
        songToSave.setUploader(userService.getCurrentUser());
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
