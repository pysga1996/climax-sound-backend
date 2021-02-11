package com.alpha.controller;

import com.alpha.constant.CrossOriginConfig;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.service.AlbumService;
import com.alpha.service.SongService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = {CrossOriginConfig.Origins.ALPHA_SOUND, CrossOriginConfig.Origins.LOCAL_HOST},
        allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})@RestController
@RequestMapping("/api/album")
public class AlbumRestController {

    private final AlbumService albumService;

    private final SongService songService;

    public AlbumRestController(AlbumService albumService, SongService songService) {
        this.albumService = albumService;
        this.songService = songService;
    }


    @GetMapping(value = "/list")
    public ResponseEntity<Page<AlbumDTO>> albumList(Pageable pageable) {
        Page<AlbumDTO> albumList = this.albumService.findAll(pageable);
        if (albumList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(albumList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<AlbumDTO> albumDetail(@RequestParam("id") Long id) {
        Optional<AlbumDTO> album = this.albumService.findById(id);
        if (album.isPresent()) {
            this.songService.setLike(album.get().getSongs());
            return new ResponseEntity<>(album.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<AlbumDTO>> albumSearch(@RequestParam String name, Pageable pageable) {
        Page<AlbumDTO> filteredAlbumList = this.albumService.findAllByTitleContaining(name, pageable);
        boolean isEmpty = filteredAlbumList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredAlbumList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload")
    public ResponseEntity<Long> createAlbum(@Valid @RequestPart("album") AlbumDTO album,
                                            @RequestPart(value = "cover", required = false)
                                                    MultipartFile file) {
        try {
            this.albumService.uploadAndSaveAlbum(file, album);
            return new ResponseEntity<>(album.getId(), HttpStatus.OK);
        } catch (Exception e) {
            if (album.getId() != null) {
                this.albumService.deleteById(album.getId());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<Void> editAlbum(@Valid @RequestPart("album") AlbumDTO album,
                                          @RequestPart(value = "cover", required = false)
                                                  MultipartFile file,
                                          @RequestParam("id") Long id) throws IOException {
        if (this.albumService.editAlbum(file, album, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteAlbum(@RequestParam("id") Long id) {
        Collection<SongDTO> songsToDelete = new ArrayList<>();
        Iterable<SongDTO> songs = songService.findAllByAlbum_Id(id);
        songs.forEach(songsToDelete::add);
        this.songService.deleteAll(songsToDelete);
        this.albumService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
