package com.lambda.controller;

import com.lambda.model.entity.Artist;
import com.lambda.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artist")
public class ArtistRestController {
    @Autowired
    ArtistService artistService;

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
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
