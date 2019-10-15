package com.lambda.service;

import com.lambda.model.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PlaylistService {
    boolean checkSongExistence(Playlist playlist, Long songId);
    Optional<Playlist> findById(Long id);
    Iterable<Playlist> getPlaylistListToAdd(Long songId);
    Page<Playlist> findAllByUser_Id(Long userId, Pageable pageable);
    Page<Playlist> findAllByTitleContaining(String title, Pageable pageable);
    void save(Playlist playlist);
    void deleteById(Long id);
    boolean addSongToPlaylist(Long songId, Long playlistId);
    boolean deleteSongFromPlaylist(Long songId, Long playlistId);
    boolean checkPlaylistOwner(Long id);
}
