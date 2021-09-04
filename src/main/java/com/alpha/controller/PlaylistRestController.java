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
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<PlaylistDTO> playlistDetail(@RequestParam("id") Long id) {
        Optional<PlaylistDTO> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@Valid @RequestBody PlaylistDTO playlist) {
        PlaylistDTO playlistDTO = this.playlistService.create(playlist);
        return new ResponseEntity<>(playlistDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<String> editPlaylist(@Valid @RequestBody PlaylistDTO playlist, @RequestParam("id") Long id) {
        Optional<PlaylistDTO> oldPlaylist = playlistService.findById(id);
        if (oldPlaylist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                playlist.setId(id);
                playlist.setUsername(this.userService.getCurrentUser().getName());
                playlist.setSongs(oldPlaylist.get().getSongs());
                this.playlistService.save(playlist);
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>("Playlist not found!", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deletePlaylist(@RequestParam Long id) {
        if (playlistService.checkPlaylistOwner(id)) {
            playlistService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/add-song/{id}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable("id") Long playlistId,
        @RequestBody List<Long> songIds) {
        this.playlistService.addSongToPlaylist(playlistId, songIds);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/remove-song")
    public ResponseEntity<Void> removeSongFromPlaylist(@RequestParam("song-id") Long songId, @RequestParam("playlist-id") Long playlistId) {
        if (playlistService.checkPlaylistOwner(playlistId)) {
            boolean result = playlistService.deleteSongFromPlaylist(songId, playlistId);
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
