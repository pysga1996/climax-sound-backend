package com.alpha.controller;

import com.alpha.model.dto.GenreDTO;
import com.alpha.service.GenreService;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genre")
public class GenreRestController {

    private GenreService genreService;

    @Autowired
    public void setGenreService(GenreService genreService) {
        this.genreService = genreService;
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @GetMapping("/list")
    public ResponseEntity<Page<GenreDTO>> genreList(Pageable pageable) {
        Page<GenreDTO> genreList = this.genreService.findAll(pageable);
        if (genreList.getTotalElements() > 0) {
            return new ResponseEntity<>(genreList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<GenreDTO> genreDetail(Integer id) {
        Optional<GenreDTO> country = this.genreService.findById(id);
        return country.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createGenre(@Valid @RequestBody GenreDTO genre) {
        try {
            this.genreService.save(genre);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @PutMapping(value = "/edit")
    public ResponseEntity<Void> editGenre(@Valid @RequestBody GenreDTO genre) {
        try {
            this.genreService.save(genre);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteGenre(@Valid @RequestParam Integer id) {
        try {
            this.genreService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
