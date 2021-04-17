package com.alpha.service;

import com.alpha.model.dto.PlaylistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PlaylistService {

    boolean checkSongExistence(PlaylistDTO playlist, Long songId);

    Optional<PlaylistDTO> findById(Long id);

    Iterable<PlaylistDTO> getPlaylistListToAdd(Long songId);

    Page<PlaylistDTO> findAllByUsername(String username, Pageable pageable);

    Page<PlaylistDTO> findAllByTitleContaining(String title, Pageable pageable);

    void save(PlaylistDTO playlist);

    void deleteById(Long id);

    boolean addSongToPlaylist(Long songId, Long playlistId);

    boolean deleteSongFromPlaylist(Long songId, Long playlistId);

    boolean checkPlaylistOwner(Long id);
}
