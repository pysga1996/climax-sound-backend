package com.lambda.controller;

import com.lambda.model.entity.Playlist;
import com.lambda.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/playlist")
public class PlaylistRestController {
    @Autowired
    PlaylistService playlistService;

    @GetMapping(params = "action=list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Playlist>> playlistList(Pageable pageable) {
        Page<Playlist> playlistList = playlistService.findAll(pageable);
        boolean isEmpty = playlistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @GetMapping(params = "action=search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Playlist>> playlistSearch(@RequestParam String name, Pageable pageable) {
        Page<Playlist> filteredPlaylistList = playlistService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredPlaylistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }

    @PostMapping(params = "action=create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createPlaylist(@Valid @RequestBody Playlist playlist) {
        if (playlist == null) {
            return new ResponseEntity<>("Playlist name has already existed in database!", HttpStatus.BAD_REQUEST);
        } else {
            playlistService.save(playlist);
            return new ResponseEntity<>("Playlist created successfully!", HttpStatus.CREATED);
        }
    }

    @PutMapping(params = {"action=edit", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editPlaylist(@Valid @RequestBody Playlist playlist, @RequestParam Long id) {
        playlist.setId(id);
        playlistService.save(playlist);
        return new ResponseEntity<>("Playlist updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping(params = {"action=delete", "id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deletePlaylist(@RequestParam Long id) {
        playlistService.deleteById(id);
        return new ResponseEntity<>("Playlist removed successfully!", HttpStatus.OK);
    }
}
