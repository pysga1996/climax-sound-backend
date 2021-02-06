package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import com.alpha.repositories.PlaylistRepository;
import com.alpha.service.PlaylistService;
import com.alpha.service.SongService;
import com.alpha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    private final SongService songService;

    private final UserService userService;

    @Autowired
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongService songService, UserService userService) {
        this.playlistRepository = playlistRepository;
        this.songService = songService;
        this.userService = userService;
    }

    @Override
    public boolean checkSongExistence(Playlist playlist, Long songId) {
        Collection<Song> songList = playlist.getSongs();
        for (Song song : songList) {
            if (song.getId().equals(songId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        return playlistRepository.findById(id);
    }

    @Override
    public Page<Playlist> findAllByUser_Id(Long userId, Pageable pageable) {
        return playlistRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Playlist> findAllByTitleContaining(String title, Pageable pageable) {
        return playlistRepository.findAllByTitleContaining(title, pageable);
    }

    @Override
    public void save(Playlist playlist) {
        playlistRepository.save(playlist);
    }

    @Override
    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }

    @Override
    public boolean addSongToPlaylist(Long songId, Long playlistId) {
        Optional<Song> song = songService.findById(songId);
        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        if (song.isPresent() && playlist.isPresent()) {
            Collection<Song> songList = playlist.get().getSongs();
            for (Song checkedSong : songList) {
                if (checkedSong.getId().equals(song.get().getId())) return false;
            }
            songList.add(song.get());
            playlist.get().setSongs(songList);
            playlistRepository.save(playlist.get());
            return true;
        }
        return false;

    }

    @Override
    public boolean deleteSongFromPlaylist(Long songId, Long playlistId) {
        Optional<Playlist> playlist = this.findById(playlistId);
        if (playlist.isPresent()) {
            Collection<Song> songList = playlist.get().getSongs();
            songList.removeIf(song -> (song.getId().equals(songId)));
            playlist.get().setSongs(songList);
            playlistRepository.save(playlist.get());
            return true;
        } else return false;
    }

    @Override
    public boolean checkPlaylistOwner(Long id) {
        Optional<Playlist> playlist = findById(id);
        UserDTO currentUser = userService.getCurrentUser();
        if (playlist.isPresent() && currentUser.getId() != null) {
            return playlist.get().getUser().getId().equals(currentUser.getId());
        } else return false;
    }

    @Override
    public Iterable<Playlist> getPlaylistListToAdd(Long songId) {
        UserDTO currentUser = userService.getCurrentUser();
        Optional<Song> song = songService.findById(songId);
        return song.map(value -> playlistRepository.findAllByUserIdAndSongsNotContains(currentUser.getId(), value)).orElse(null);
    }
}
