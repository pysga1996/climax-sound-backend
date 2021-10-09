package com.alpha.service;

import com.alpha.model.dto.PlaylistDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlaylistService {

    boolean checkSongExistence(PlaylistDTO playlistDTO, Long songId);

    PlaylistDTO detail(Long id);

    Page<PlaylistDTO> getPlaylistListToAdd(Long songId, Pageable pageable);

    Page<PlaylistDTO> findAllByUsername(String username, Pageable pageable);

    Page<PlaylistDTO> findAllByTitleContaining(String title, Pageable pageable);

    PlaylistDTO create(PlaylistDTO playlistDTO);

    void update(Long id, PlaylistDTO playlistDTO);

    void deleteById(Long id);

    void addSongToPlaylist(Long songId, List<Long> playlistId);

    void deleteSongFromPlaylist(Long playlistId, List<Long> songIds);
}
