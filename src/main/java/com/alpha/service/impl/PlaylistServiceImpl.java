package com.alpha.service.impl;

import com.alpha.constant.ModelStatus;
import com.alpha.mapper.PlaylistMapper;
import com.alpha.model.dto.PlaylistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import com.alpha.repositories.PlaylistRepository;
import com.alpha.repositories.SongRepository;
import com.alpha.service.PlaylistService;
import com.alpha.service.UserService;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final SongRepository songRepository;

    private final UserService userService;

    private final PlaylistMapper playlistMapper;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository,
        UserService userService, PlaylistMapper playlistMapper) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userService = userService;
        this.playlistMapper = playlistMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkSongExistence(PlaylistDTO playlistDTO, Long songId) {
        Collection<SongDTO> songList = playlistDTO.getSongs();
        for (SongDTO song : songList) {
            if (song.getId().equals(songId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public PlaylistDTO detail(Long id) {
        String username = this.userService.getCurrentUsername();
        return this.playlistRepository.findByIdAndUsername(id, username)
            .map(this.playlistMapper::entityToDto)
            .orElseThrow(() -> new EntityNotFoundException("Playlist not found!"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistDTO> findAllByUsername(String username, Pageable pageable) {
        Page<Playlist> playlistPage = this.playlistRepository.findAllByUsername(username, pageable);
        return new PageImpl<>(playlistPage.getContent()
            .stream()
            .map(this.playlistMapper::entityToDto)
            .collect(Collectors.toList()), pageable, playlistPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistDTO> findAllByTitleContaining(String title, Pageable pageable) {
        Page<Playlist> playlistPage = this.playlistRepository
            .findAllByTitleContaining(title, pageable);
        return new PageImpl<>(playlistPage.getContent()
            .stream()
            .map(this.playlistMapper::entityToDto)
            .collect(Collectors.toList()), pageable, playlistPage.getTotalElements());
    }

    @Override
    @Transactional
    public PlaylistDTO create(PlaylistDTO playlistDTO) {
        String username = this.userService.getCurrentUsername();
        boolean existsPlaylistByTitleAndUsername = this.playlistRepository
            .existsPlaylistByTitleAndUsername(playlistDTO.getTitle(), username);
        if (existsPlaylistByTitleAndUsername) {
            throw new EntityExistsException("Playlist with that name exist!");
        }
        playlistDTO.setUsername(username);
        Playlist newPlaylist = this.playlistMapper.dtoToEntity(playlistDTO);
        newPlaylist.setUsername(username);
        newPlaylist.setCreateTime(new Date());
        newPlaylist.setStatus(ModelStatus.ACTIVE);
        newPlaylist = this.playlistRepository.saveAndFlush(newPlaylist);
        return this.playlistMapper.entityToDtoPure(newPlaylist);
    }

    @Override
    @Transactional
    public void update(Long id, PlaylistDTO playlistDTO) {
        String username = this.userService.getCurrentUsername();
        Optional<Playlist> optionalPlaylist = this.playlistRepository
            .findByIdAndUsername(id, username);
        if (optionalPlaylist.isPresent()) {
            Playlist playlist = optionalPlaylist.get();
            playlist.setTitle(playlistDTO.getTitle());
            playlist.setUpdateTime(new Date());
            this.playlistRepository.save(playlist);
        } else {
            throw new EntityNotFoundException("Playlist not found");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        String username = this.userService.getCurrentUsername();
        Optional<Playlist> optionalPlaylist = this.playlistRepository.findByIdAndUsername(id, username);
        if (optionalPlaylist.isPresent()) {
            Playlist playlist = optionalPlaylist.get();
            playlist.setStatus(ModelStatus.INACTIVE);
            playlist.setUpdateTime(new Date());
            this.playlistRepository.saveAndFlush(optionalPlaylist.get());
        } else {
            throw new EntityNotFoundException("Playlist not found!");
        }
    }

    @Override
    @Transactional
    public void addSongToPlaylist(Long playlistId, List<Long> songIds) {
        String username = this.userService.getCurrentUsername();
        Optional<Playlist> playlist = this.playlistRepository
            .findByIdAndUsername(playlistId, username);
        if (playlist.isPresent()) {
            this.playlistRepository.addToPlayList(username, playlistId, songIds);
        } else {
            throw new EntityNotFoundException("Playlist not found!");
        }
    }

    @Override
    @Transactional
    public void deleteSongFromPlaylist(Long playlistId, List<Long> songIds) {
        String username = this.userService.getCurrentUsername();
        Optional<Playlist> playlist = this.playlistRepository
            .findByIdAndUsername(playlistId, username);
        if (playlist.isPresent()) {
            this.playlistRepository.removeFromPlaylist(username, playlistId, songIds);
        } else {
            throw new EntityNotFoundException("Playlist not found!");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistDTO> getPlaylistListToAdd(Long songId,
        Pageable pageable) {
        String username = this.userService.getCurrentUsername();
        Optional<Song> song = this.songRepository.findById(songId);
        return song.map(value -> this.playlistRepository
            .findAllByUsernameAndSongsNotContains(username, value, pageable)
            .map(this.playlistMapper::entityToDto))
            .orElseGet(Page::empty);
    }
}
