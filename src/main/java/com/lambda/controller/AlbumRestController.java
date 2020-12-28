package com.lambda.controller;

import com.lambda.model.entities.Album;
import com.lambda.model.entities.Song;
import com.lambda.service.AlbumService;
import com.lambda.service.ArtistService;
import com.lambda.service.SongService;
import com.lambda.service.UserService;
import com.lambda.service.impl.CoverStorageService;
import com.lambda.service.impl.DownloadService;
import com.lambda.service.impl.FormConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin(origins = {"https://climax-sound.netlify.com", "http://localhost:4200"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/album")
public class AlbumRestController {
    private AlbumService albumService;

    @Autowired
    public void setAlbumService(AlbumService albumService) {
        this.albumService = albumService;
    }

    private SongService songService;

    @Autowired
    public void setSongService(SongService songService) {
        this.songService = songService;
    }

    private ArtistService artistService;

    @Autowired
    public void setArtistService(ArtistService artistService) {
        this.artistService = artistService;
    }

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private CoverStorageService coverStorageService;

    @Autowired
    public void setCoverStorageService(CoverStorageService coverStorageService) {
        this.coverStorageService = coverStorageService;
    }

    private FormConvertService formConvertService;

    @Autowired
    public void setFormConvertService(FormConvertService formConvertService) {
        this.formConvertService = formConvertService;
    }

    private DownloadService downloadService;

    @Autowired
    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<Page<Album>> albumList(Pageable pageable) {
        Page<Album> albumList = albumService.findAll(pageable);
        if (albumList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(albumList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Album> albumDetail(@RequestParam("id") Long id) {
        Optional<Album> album = albumService.findById(id);
        if (album.isPresent()) {
            songService.setLike(album.get().getSongs());
            return new ResponseEntity<>(album.get(), HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<Page<Album>> albumSearch(@RequestParam String name, Pageable pageable) {
        Page<Album> filteredAlbumList = albumService.findAllByTitleContaining(name, pageable);
        boolean isEmpty = filteredAlbumList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(filteredAlbumList, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload")
    public ResponseEntity<Long> createAlbum(@Valid @RequestPart("album") Album album, @RequestPart(value = "cover", required = false) MultipartFile file) {
        try {
            albumService.save(album);
            if (file != null) {
                String fileName = coverStorageService.saveToFirebaseStorage(album, file);
                album.setCoverUrl(fileName);
            }
            album.setUploader(userService.getCurrentUser());
            albumService.save(album);
            return new ResponseEntity<>(album.getId(), HttpStatus.OK);
        } catch (Exception e) {
            if (album.getId() != null) {
                albumService.deleteById(album.getId());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<Void> editAlbum(@Valid @RequestPart("album") Album album, @RequestPart(value = "cover", required = false) MultipartFile file, @RequestParam("id") Long id) {
        if (file != null) {
            String fileName = coverStorageService.saveToFirebaseStorage(album, file);
            album.setCoverUrl(fileName);
        }
        Optional<Album> oldAlbum = albumService.findById(id);
        if (oldAlbum.isPresent()) {
            albumService.setFields(oldAlbum.get(), album);
            albumService.save(oldAlbum.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteAlbum(@RequestParam("id") Long id) {
        Collection<Song> songsToDelete = new ArrayList<>();
        Iterable<Song> songs = songService.findAllByAlbum_Id(id);
        songs.forEach(songsToDelete::add);
        songService.deleteAll(songsToDelete);
        albumService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
