package com.lambda.controller;

import com.lambda.model.entity.Album;
import com.lambda.model.entity.Song;
import com.lambda.model.form.AlbumForm;
import com.lambda.service.AlbumService;
import com.lambda.service.ArtistService;
import com.lambda.service.SongService;
import com.lambda.service.impl.FormConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/album")
public class AlbumRestController {
    @Autowired
    AlbumService albumService;

    @Autowired
    SongService songService;

    @Autowired
    ArtistService artistService;

    @Autowired
    FormConvertService formConvertService;

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Album>> albumList(Pageable pageable) {
        Page<Album> albumList = albumService.findAll(pageable);
        if (albumList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(albumList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Album> albumDetail(@RequestParam("id") Long id) {
        Optional<Album> album = albumService.findById(id);
        return album.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Album>> albumSearch(@RequestParam String name, Pageable pageable) {
        Page<Album> filteredAlbumList = albumService.findAllByNameContaining(name, pageable);
        boolean isEmpty = filteredAlbumList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredAlbumList, HttpStatus.OK);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAlbum(@Valid @RequestBody AlbumForm albumForm) {
        Album album = formConvertService.convertToAlbum(albumForm);
        if (album != null) {
            albumService.save(album);
            return new ResponseEntity<>("Album created successfully!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>("Album has already already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PutMapping(value = "edit", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> editAlbum(@Valid @RequestBody AlbumForm albumForm, @RequestParam Long id) {
        Album album = formConvertService.convertToAlbum(albumForm);
        if (album != null) {
            album.setId(id);
            albumService.save(album);
            return new ResponseEntity<>("Album updated successfully!", HttpStatus.OK);
        } else return new ResponseEntity<>("Album has already existed in database!", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping(value = "delete", params = {"id"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteAlbum(@RequestParam Long id) {
        Collection<Song> songsToDelete = new ArrayList<>();
        Iterable<Song> songs = songService.findAllByAlbum_Id(id);
        songs.forEach(songsToDelete::add);
        songService.deleteAll(songsToDelete);
        albumService.deleteById(id);
        return new ResponseEntity<>("Album removed successfully!", HttpStatus.OK);
    }

}
