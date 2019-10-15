package com.lambda.controller;

import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import com.lambda.service.ArtistService;
import com.lambda.service.SongService;
import com.lambda.service.impl.AvatarStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/artist")
public class ArtistRestController {
    @Autowired
    ArtistService artistService;

    @Autowired
    SongService songService;

    @Autowired
    AvatarStorageService avatarStorageService;

    @GetMapping(value = "/search", params = "name")
    public ResponseEntity<Iterable<Artist>> searchArtistByName(@RequestParam("name") String name) {
        Iterable<Artist> artistList = artistService.findTop10ByNameContaining(name);
        long size = 0;
        if (artistList instanceof Collection) {
            size = ((Collection<Artist>) artistList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<Artist>> getArtistList(Pageable pageable) {
        Page<Artist> artistList = artistService.findAll(pageable);
        if (artistList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Artist> artistDetail(@RequestParam("id") Long id) {
        Optional<Artist> artist = artistService.findById(id);
        return artist.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArtist(@RequestPart("artist") Artist artist, @RequestPart("avatar") MultipartFile multipartFile) {
        artistService.save(artist);
        String fileDownloadUri = avatarStorageService.saveToFirebaseStorage(artist, multipartFile);
        artist.setAvatarUrl(fileDownloadUri);
        artistService.save(artist);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/detail")
    public ResponseEntity<Artist> getArtistDetail(@RequestParam("id") Long id) {
        Optional<Artist> artist = artistService.findById(id);
        if (artist.isPresent()) {
            return new ResponseEntity<>(artist.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<Void> updateArtist(@RequestParam("id") Long id, @RequestPart("artist") Artist artist, @RequestPart(value = "avatar", required = false) MultipartFile multipartFile) {
        Optional<Artist> oldArtist = artistService.findById(id);
        if (oldArtist.isPresent()) {
            if (multipartFile != null) {
                String fileDownloadUri = avatarStorageService.saveToFirebaseStorage(oldArtist.get(), multipartFile);
                artist.setAvatarUrl(fileDownloadUri);
            }
            artistService.setFields(oldArtist.get(), artist);
            artistService.save(oldArtist.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<Void> deleteArtist(@RequestParam("id") Long id) {
        artistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/song-list")
    public ResponseEntity<Page<Song>> getSongs(@RequestParam("artist-id") Long id, Pageable pageable) {
        Optional<Artist> artist = artistService.findById(id);
        if (artist.isPresent()) {
            Page<Song> songs = songService.findAllByArtistsContains(artist.get(), pageable);
            return new ResponseEntity<>(songs, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
