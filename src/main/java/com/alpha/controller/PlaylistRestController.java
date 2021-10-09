package com.alpha.controller;

import com.alpha.model.dto.PlaylistDTO;
import com.alpha.service.PlaylistService;
import com.alpha.service.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistRestController {

    private PlaylistService playlistService;
    private UserService userService;

    @Autowired
    public void setPlaylistService(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<PlaylistDTO>> playlistList(Pageable pageable) {
        String username = userService.getCurrentUsername();
        Page<PlaylistDTO> playlistList = playlistService.findAllByUsername(username, pageable);
        return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/list-to-add")
    public ResponseEntity<Page<PlaylistDTO>> showPlaylistListToAdd(
        @RequestParam("song-id") Long songId, Pageable pageable) {
        Page<PlaylistDTO> filteredPlaylistList = this.playlistService
            .getPlaylistListToAdd(songId, pageable);
        return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<PlaylistDTO> playlistDetail(@PathVariable("id") Long id) {
        PlaylistDTO playlist = playlistService.detail(id);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO playlist) {
        PlaylistDTO playlistDTO = this.playlistService.create(playlist);
        return new ResponseEntity<>(playlistDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<Void> editPlaylist(@PathVariable("id") Long id,
        @Valid @RequestBody PlaylistDTO playlist) {
        this.playlistService.update(id, playlist);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("id") Long id) {
        this.playlistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/add-song/{id}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable("id") Long playlistId,
        @RequestBody List<Long> songIds) {
        this.playlistService.addSongToPlaylist(playlistId, songIds);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/remove-song/{id}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable("id") Long playlistId,
        @RequestBody List<Long> songIds) {
        this.playlistService.deleteSongFromPlaylist(playlistId, songIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
