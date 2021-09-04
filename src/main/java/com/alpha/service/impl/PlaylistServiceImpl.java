package com.alpha.service.impl;

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
    public boolean checkSongExistence(PlaylistDTO playlist, Long songId) {
        Collection<SongDTO> songList = playlist.getSongs();
        for (SongDTO song : songList) {
            if (song.getId().equals(songId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlaylistDTO> findById(Long id) {
        return this.playlistRepository.findById(id)
                .map(this.playlistMapper::entityToDto);
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
    public void save(PlaylistDTO playlist) {
        this.playlistRepository.save(this.playlistMapper.dtoToEntity(playlist));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }

    @Override
    @Transactional
    public boolean addSongToPlaylist(Long songId, Long playlistId) {
        Optional<Song> song = this.songRepository.findById(songId);
        Optional<Playlist> playlist = this.playlistRepository.findById(playlistId);
        if (song.isPresent() && playlist.isPresent()) {
            Collection<Song> songList = playlist.get().getSongs();
            for (Song checkedSong : songList) {
                if (checkedSong.getId().equals(song.get().getId())) return false;
            }
            songList.add(song.get());
            playlist.get().setSongs(songList);
            this.playlistRepository.save(playlist.get());
            return true;
        }
        return false;

    }

    @Override
    @Transactional
    public boolean deleteSongFromPlaylist(Long songId, Long playlistId) {
        Optional<Playlist> playlist = this.playlistRepository.findById(playlistId);
        if (playlist.isPresent()) {
            Collection<Song> songList = playlist.get().getSongs();
            songList.removeIf(song -> (song.getId().equals(songId)));
            playlist.get().setSongs(songList);
            this.playlistRepository.save(playlist.get());
            return true;
        } else return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkPlaylistOwner(Long id) {
        Optional<Playlist> playlist = this.playlistRepository.findById(id);
        OAuth2AuthenticatedPrincipal currentUser = this.userService.getCurrentUser();
        return playlist.filter(value -> currentUser.getName().equals(value.getUsername())).isPresent();
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
