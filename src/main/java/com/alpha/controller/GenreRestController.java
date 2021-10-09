package com.alpha.controller;

import com.alpha.model.dto.GenreDTO;
import com.alpha.service.GenreService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genre")
public class GenreRestController {

    private GenreService genreService;

    @Autowired
    public void setGenreService(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<GenreDTO>> genreList(Pageable pageable) {
        return ResponseEntity.ok(this.genreService.findAll(pageable));
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<GenreDTO> genreDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(this.genreService.findById(id));
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @PostMapping(value = "/create")
    public ResponseEntity<GenreDTO> createGenre(@Valid @RequestBody GenreDTO genre) {
        GenreDTO createGenre = this.genreService.create(genre);
        return ResponseEntity.ok(createGenre);
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<GenreDTO> editGenre(@PathVariable("id") Integer id,
        @Valid @RequestBody GenreDTO genre) {
        GenreDTO updatedGenre = this.genreService.update(id, genre);
        return ResponseEntity.ok(updatedGenre);
    }

    @PreAuthorize("hasAuthority(@Authority.GENRE_MANAGEMENT)")
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer id) {
        this.genreService.delete(id);
        return ResponseEntity.ok().build();
    }
}
