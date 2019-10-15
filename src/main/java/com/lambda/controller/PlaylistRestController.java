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

    public boolean isOwnerOfPlaylist(Long playlistId) {
        System.out.println(playlistId);
//        Optional<Playlist> playlist = playlistService.findById(Long.parseLong(id));
        Optional<Playlist> playlist = playlistService.findById(playlistId);
        User currentUser = userDetailService.getCurrentUser();
        if (playlist.isPresent() && currentUser.getId()!=null) {
            return playlist.get().getUser().getId().equals(currentUser.getId());
        }
        return false;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<Playlist>> playlistList(Pageable pageable) {
        User currentUser = userDetailService.getCurrentUser();
        Page<Playlist> playlistList = playlistService.findAllByUser_Id(currentUser.getId(), pageable);
        boolean isEmpty = playlistList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(playlistList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Playlist> playlistDetail(@RequestParam("id") Long id) {
        Optional<Playlist> playlist = playlistService.findById(id);
        if (playlist.isPresent()) {
            if (playlistService.checkPlaylistOwner(id)) {
                return new ResponseEntity<>(playlist.get(), HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/create")
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
            if (playlistService.checkPlaylistOwner(id)) {
                playlist.setId(id);
                playlist.setUser(userDetailService.getCurrentUser());
                playlist.setSongs(oldPlaylist.get().getSongs());
                playlistService.save(playlist);
                return new ResponseEntity<>(HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>("Playlist not found!", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deletePlaylist(@RequestParam Long id) {
        if (playlistService.checkPlaylistOwner(id)) {
            playlistService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
    public ResponseEntity<Iterable<Playlist>> showPlaylistListToAdd(@RequestParam("songId") Long songId) {
//        User currentUser = userDetailService.getCurrentUser();
//        Iterable<Playlist> playlistList = playlistService.findAllByUser_Id(currentUser.getId());
//        Collection<Playlist> playlistCollection = new ArrayList<>();
//        for (Playlist playlist: playlistList) {
//            playlistCollection.add(playlist);
//        }
//        List<Playlist> filteredPlaylistList = new ArrayList<>();
//        for (Playlist playlist: playlistCollection) {
//            if (!playlistService.checkSongExistence(playlist, songId)) {
//                filteredPlaylistList.add(playlist);
//            }
//        }
        Iterable<Playlist> filteredPlaylistList = playlistService.getPlaylistListToAdd(songId);
//        boolean isEmpty = filteredPlaylistList.iterator();
//        if (isEmpty) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else return new ResponseEntity<>(filteredPlaylistList, HttpStatus.OK);
        return null;
    }
}
