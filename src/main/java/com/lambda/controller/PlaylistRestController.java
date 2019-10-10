package com.lambda.controller;

import com.lambda.model.entity.Playlist;
import com.lambda.model.entity.User;
import com.lambda.service.PlaylistService;
import com.lambda.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/playlist")
public class PlaylistRestController {
    @Autowired
    PlaylistService playlistService;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Playlist>> playlistList(Pageable pageable) {
        User currentUser = userDetailService.getCurrentUser();
        Page<Playlist> playlistList = playlistService.findAllByUser_Id(currentUser.getId(), pageable);
        boolean isEmpty = playlistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Playlist> playlistDetail(@RequestParam("id") Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Playlist>> playlistSearch(@RequestParam String name, Pageable pageable) {
        Page<Playlist> filteredPlaylistList = playlistService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredPlaylistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createPlaylist(@Valid @RequestBody Playlist playlist) {
        if (playlist == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            playlist.setUser(userDetailService.getCurrentUser());
            playlistService.save(playlist);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    @PutMapping(value = "/edit", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editPlaylist(@Valid @RequestBody Playlist playlist, @RequestParam Long id) {
        Optional<Playlist> oldPlaylist = playlistService.findById(id);
        if (oldPlaylist.isPresent()) {
            playlist.setId(id);
            playlist.setUser(userDetailService.getCurrentUser());
            playlist.setSongs(oldPlaylist.get().getSongs());
            playlistService.save(playlist);
            return new ResponseEntity<>("Playlist updated successfully!", HttpStatus.OK);
        } else return new ResponseEntity<>("Playlist not found!", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePlaylist(@RequestParam Long id) {
        playlistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/add-song")
    public ResponseEntity<Void> addSongToPlaylist(@RequestParam("songId") Long songId, @RequestParam("playlistId") Long playlistId) {
        boolean result = playlistService.addSongToPlaylist(songId, playlistId);
        if (result) {
            return new ResponseEntity<>( HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping(value = "/remove-song")
    public ResponseEntity<String> deletePlaylistSong(@RequestParam("songId") Long songId, @RequestParam("playlistId")Long playlistId) {
        boolean result = playlistService.deleteSongFromPlaylist(songId,playlistId);
        if(result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
