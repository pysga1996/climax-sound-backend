package com.lambda.controller;

import com.lambda.model.entities.Artist;
import com.lambda.model.entities.Song;
import com.lambda.service.ArtistService;
import com.lambda.service.SongService;
import com.lambda.service.impl.AvatarStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = {"https://climax-sound.netlify.com", "http://localhost:4200"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/artist")
public class ArtistRestController {
    private ArtistService artistService;

    @Autowired
    public void setArtistService(ArtistService artistService) {
        this.artistService = artistService;
    }

    private SongService songService;

    @Autowired
    public void setSongService(SongService songService) {
        this.songService = songService;
    }

    private AvatarStorageService avatarStorageService;

    @Autowired
    public void setAvatarStorageService(AvatarStorageService avatarStorageService) {
        this.avatarStorageService = avatarStorageService;
    }

    @PreAuthorize("permitAll()")
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

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<Artist>> getArtistList(Pageable pageable) {
        Page<Artist> artistList = artistService.findAll(pageable);
        if (artistList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Artist> artistDetail(@RequestParam("id") Long id) {
        Optional<Artist> artist = artistService.findById(id);
        return artist.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArtist(@RequestPart("artist") Artist artist, @RequestPart("avatar") MultipartFile multipartFile) {
        try {
            artistService.save(artist);
            String fileDownloadUri = avatarStorageService.saveToFirebaseStorage(artist, multipartFile);
            artist.setAvatarUrl(fileDownloadUri);
            artistService.save(artist);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            artistService.deleteById(artist.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/edit", params = "id")
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
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteArtist(@RequestParam("id") Long id) {
        artistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/song-list", params = "artist-id")
    public ResponseEntity<Page<Song>> getSongListOfArtist(@RequestParam("artist-id") Long id, @PageableDefault(size = 5) Pageable pageable) {
        Optional<Artist> artist = artistService.findById(id);
        if (artist.isPresent()) {
            Page<Song> songList = songService.findAllByArtistsContains(artist.get(), pageable);
            if (songList.getTotalElements() > 0) {
                songService.setLike(songList);
                return new ResponseEntity<>(songList, HttpStatus.OK);
            } return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
