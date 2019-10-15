package com.lambda.controller;

import com.lambda.configuration.security.WebSecurity;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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

    @PreAuthorize("isOwnerOfPlaylist(#id)")
    @GetMapping(value = "/detail", params = "id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Playlist> playlistDetail(@P("id") @RequestParam("id") Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Playlist>> playlistSearch(@RequestParam String title, Pageable pageable) {
        Page<Playlist> filteredPlaylistList = playlistService.findAllByTitleContaining(title, pageable);
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
    public ResponseEntity<Void> removeSongFromPlaylist(@RequestParam("songId") Long songId, @RequestParam("playlistId")Long playlistId) {
        boolean result = playlistService.deleteSongFromPlaylist(songId,playlistId);
        if(result) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/list-to-add")
    public ResponseEntity<Collection<Playlist>> showPlaylistListToAdd(@RequestParam("songId") Long songId) {
        User currentUser = userDetailService.getCurrentUser();
        Iterable<Playlist> playlistList = playlistService.findAllByUser_Id(currentUser.getId());
        Collection<Playlist> playlistCollection = new ArrayList<>();
        for (Playlist playlist: playlistList) {
            playlistCollection.add(playlist);
        }
        List<Playlist> filteredPlaylistList = new ArrayList<>();
        for (Playlist playlist: playlistCollection) {
            if (!playlistService.checkSongExistence(playlist, songId)) {
                filteredPlaylistList.add(playlist);
            }
        }
        boolean isEmpty = filteredPlaylistList.size() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
    }
}
