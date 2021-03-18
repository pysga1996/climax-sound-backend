package com.alpha.controller;

import com.alpha.model.dto.GenreDTO;
import com.alpha.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api/genre")
public class GenreRestController {
    private GenreService genreService;

    @Autowired
    public void setGenreService(GenreService genreService) {
        this.genreService = genreService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Page<GenreDTO>> genreList(Pageable pageable) {
        Page<GenreDTO> genreList = this.genreService.findAll(pageable);
        if (genreList.getTotalElements() > 0) {
            return new ResponseEntity<>(genreList, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<GenreDTO> genreDetail(Integer id) {
        Optional<GenreDTO> country = this.genreService.findById(id);
        return country.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createGenre(@Valid @RequestBody GenreDTO genre) {
        try {
            this.genreService.save(genre);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/edit")
    public ResponseEntity<Void> editGenre(@Valid @RequestBody GenreDTO genre) {
        try {
            this.genreService.save(genre);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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
