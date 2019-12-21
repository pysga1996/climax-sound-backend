package com.lambda.services.impl;

import com.lambda.models.entities.Playlist;
import com.lambda.models.entities.Song;
import com.lambda.models.entities.User;
import com.lambda.repositories.PlaylistRepository;
import com.lambda.services.PlaylistService;
import com.lambda.services.SongService;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {
    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    SongService songService;

    @Autowired
    UserService userService;

    @Override
    public boolean checkSongExistence(Playlist playlist, Long songId) {
        Collection<Song> songList = playlist.getSongs();
        for (Song song: songList) {
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
        return playlistRepository.findAllByUser_Id(userId, pageable);
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
    public boolean addSongToPlaylist(Long songId, Long playlistId){
        Optional<Song> song = songService.findById(songId);
        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        if (song.isPresent() && playlist.isPresent()){
            Collection<Song> songList = playlist.get().getSongs();
            for (Song checkedSong: songList) {
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
        if(playlist.isPresent()) {
            Collection<Song> songList = playlist.get().getSongs();
            Collection<Song> newSongList = new HashSet<>();
            for (Song song: songList) {
                if (!song.getId().equals(songId)) {
                    newSongList.add(song);
                }
            }
            playlist.get().setSongs(newSongList);
            playlistRepository.save(playlist.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPlaylistOwner(Long id) {
        Optional<Playlist> playlist = findById(id);
        User currentUser = userService.getCurrentUser();
        if (playlist.isPresent() && currentUser.getId()!=null) {
            return playlist.get().getUser().getId().equals(currentUser.getId());
        } else return false;
    }

    @Override
    public Iterable<Playlist> getPlaylistListToAdd(Long songId) {
        User currentUser = userService.getCurrentUser();
        Optional<Song> song = songService.findById(songId);
        return song.map(value -> playlistRepository.findAllByUser_IdAndSongsNotContains(currentUser.getId(), value)).orElse(null);
    }
}
