package com.alpha.controller;

import com.alpha.elastic.model.ArtistEs;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.ArtistSearchDTO;
import com.alpha.service.ArtistService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/artist")
public class ArtistRestController {

    private final ArtistService artistService;

    @Autowired
    public ArtistRestController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/search")
    public ResponseEntity<Page<ArtistDTO>> searchArtistByName(Pageable pageable,
        @ModelAttribute ArtistSearchDTO artistSearchDTO) {
        Page<ArtistDTO> artistList = this.artistService.findByConditions(pageable, artistSearchDTO);
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/es-search")
    public ResponseEntity<Page<ArtistEs>> searchArtistByName(@RequestParam(value = "name") String name,
        Pageable pageable) {
        Page<ArtistEs> artistList = this.artistService.findPageByName(name, pageable);
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<ArtistDTO>> getArtistList(Pageable pageable) {
        Page<ArtistDTO> artistList = this.artistService.findAll(pageable);
        return new ResponseEntity<>(artistList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<ArtistDTO> artistDetail(@PathVariable("id") Long id) {
        ArtistDTO artist = this.artistService.findById(id);
        return ResponseEntity.ok(artist);
    }

    @PreAuthorize("hasAuthority(@Authority.ARTIST_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArtist(@RequestPart("artist") ArtistDTO artist,
        @RequestPart("avatar") MultipartFile multipartFile) {
        this.artistService.create(artist, multipartFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(@Authority.ARTIST_MANAGEMENT)")
    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable("id") Long id,
        @RequestPart("artist") ArtistDTO artist,
        @RequestPart(value = "avatar", required = false) MultipartFile multipartFile)
        throws IOException {
        ArtistDTO artistDTO = this.artistService.update(id, artist, multipartFile);
        return new ResponseEntity<>(artistDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(@Authority.ARTIST_MANAGEMENT)")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable("id") Long id) {
        this.artistService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
