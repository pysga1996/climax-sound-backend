package com.alpha.controller;

import com.alpha.constant.CrossOriginConfig;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.service.ArtistService;
import com.alpha.service.SongService;
import com.alpha.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = {CrossOriginConfig.Origins.ALPHA_SOUND, CrossOriginConfig.Origins.LOCAL_HOST},
        allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})@RestController
@RequestMapping("/api/artist")
public class ArtistRestController {

    private final ArtistService artistService;

    private final SongService songService;

    private final StorageService storageService;

    @Autowired
    public ArtistRestController(ArtistService artistService, SongService songService,
                                StorageService storageService) {
        this.artistService = artistService;
        this.songService = songService;
        this.storageService = storageService;
    }


    @PreAuthorize("permitAll()")
    @GetMapping(value = "/search", params = "name")
    public ResponseEntity<Iterable<ArtistDTO>> searchArtistByName(@RequestParam("name") String name) {
        Iterable<ArtistDTO> artistList = this.artistService.findTop10ByNameContaining(name);
        long size = 0;
        if (artistList instanceof Collection) {
            size = ((Collection<ArtistDTO>) artistList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<ArtistDTO>> getArtistList(Pageable pageable) {
        Page<ArtistDTO> artistList = this.artistService.findAll(pageable);
        if (artistList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<ArtistDTO> artistDetail(@RequestParam("id") Long id) {
        Optional<ArtistDTO> artist = this.artistService.findById(id);
        return artist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArtist(@RequestPart("artist") ArtistDTO artist,
                                             @RequestPart("avatar") MultipartFile multipartFile) {
        try {
            artistService.save(artist);
            String fileDownloadUri = this.storageService.upload(multipartFile, artist);
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
    public ResponseEntity<Void> updateArtist(@RequestParam("id") Long id,
                                             @RequestPart("artist") ArtistDTO artist,
                                             @RequestPart(value = "avatar", required = false)
                                                     MultipartFile multipartFile) throws IOException {
        Optional<ArtistDTO> oldArtist = this.artistService.findById(id);
        if (oldArtist.isPresent()) {
            if (multipartFile != null) {
                String fileDownloadUri = this.storageService.upload(multipartFile, oldArtist.get());
                artist.setAvatarUrl(fileDownloadUri);
            }
            this.artistService.setFields(oldArtist.get(), artist);
            this.artistService.save(oldArtist.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteArtist(@RequestParam("id") Long id) {
        this.artistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/song-list", params = "artist-id")
    public ResponseEntity<Page<SongDTO>> getSongListOfArtist(@RequestParam("artist-id") Long id,
                                                             @PageableDefault(size = 5) Pageable pageable) {
        Optional<ArtistDTO> artist = this.artistService.findById(id);
        if (artist.isPresent()) {
            Page<SongDTO> songList = this.songService.findAllByArtistsContains(artist.get(), pageable);
            if (songList.getTotalElements() > 0) {
                this.songService.setLike(songList);
                return new ResponseEntity<>(songList, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
