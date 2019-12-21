package com.lambda.controllers;

import com.lambda.models.entities.Playlist;
import com.lambda.models.entities.User;
import com.lambda.services.PlaylistService;
import com.lambda.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = {"https://climax-sound.netlify.com", "http://localhost:4200"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/playlist")
public class PlaylistRestController {
    @Autowired
    PlaylistService playlistService;

    @Autowired
    UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<Playlist>> playlistList(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Long id = currentUser.getId();
        Page<Playlist> playlistList = playlistService.findAllByUser_Id(id, pageable);
        boolean isEmpty = playlistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Playlist> playlistDetail(@RequestParam("id") Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlaylist(@Valid @RequestBody Playlist playlist) {
        if (playlist == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            playlist.setUser(userService.getCurrentUser());
            playlistService.save(playlist);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<String> editPlaylist(@Valid @RequestBody Playlist playlist, @RequestParam("id") Long id) {
        Optional<Playlist> oldPlaylist = playlistService.findById(id);
        if (oldPlaylist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                playlist.setId(id);
                playlist.setUser(userService.getCurrentUser());
                playlist.setSongs(oldPlaylist.get().getSongs());
                playlistService.save(playlist);
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
    @PostMapping(value = "/add-song")
    public ResponseEntity<Void> addSongToPlaylist(@RequestParam("song-id") Long songId, @RequestParam("playlist-id") Long playlistId) {
        if (playlistService.checkPlaylistOwner(playlistId)) {
            boolean result = playlistService.addSongToPlaylist(songId, playlistId);
            if (result) {
                return new ResponseEntity<>( HttpStatus.OK);
            } else return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/remove-song")
    public ResponseEntity<Void> removeSongFromPlaylist(@RequestParam("song-id") Long songId, @RequestParam("playlist-id")Long playlistId) {
        if (playlistService.checkPlaylistOwner(playlistId)) {
            boolean result = playlistService.deleteSongFromPlaylist(songId, playlistId);
            if(result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list-to-add")
    public ResponseEntity<Iterable<Playlist>> showPlaylistListToAdd(@RequestParam("song-id") Long songId) {
        Iterable<Playlist> filteredPlaylistList = playlistService.getPlaylistListToAdd(songId);
        int size = 0;
        if (filteredPlaylistList instanceof Collection) {
            size = ((Collection<?>) filteredPlaylistList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }
}
