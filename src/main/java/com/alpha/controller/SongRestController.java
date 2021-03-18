package com.alpha.controller;

import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongUploadForm;
import com.alpha.model.dto.UserDTO;
import com.alpha.service.*;
import com.alpha.service.impl.FormConvertService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*", exposedHeaders = {HttpHeaders.SET_COOKIE})
@RestController
@RequestMapping("/api/song")
public class SongRestController {

    private final SongService songService;

    private final UserService userService;

    private final LikeService likeService;

    private final CommentService commentService;

    private final FormConvertService formConvertService;

    private final StorageService storageService;

    @Autowired
    public SongRestController(SongService songService, UserService userService,
                              LikeService likeService, CommentService commentService,
                              FormConvertService formConvertService, StorageService storageService) {
        this.songService = songService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.formConvertService = formConvertService;
        this.storageService = storageService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<Void> uploadSong(@Valid @RequestPart("song") SongUploadForm songUploadForm,
                                           @RequestPart("audio") MultipartFile file,
                                           @RequestParam(value = "album-albumId", required = false)
                                                       Long albumId) {
        SongDTO song = formConvertService.convertSongUploadFormToSong(songUploadForm);
        try {
            this.songService.uploadAndSaveSong(file, song, albumId);
            log.info("Upload song successfully!");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error while uploading song: ", ex);
            if (song.getId() != null) {
                this.songService.deleteById(song.getId());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<SongDTO>> songList(@PageableDefault() Pageable pageable,
                                                  @RequestParam(value = "sort", required = false) String sort) {
        Page<SongDTO> songList = this.songService.findAll(pageable, sort);
        HttpCookie httpCookie = ResponseCookie.from("pysga-alpha-sound", "shit").maxAge(300).path("/").build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, httpCookie.toString());
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
        } else {
            this.songService.setLike(songList);
            return new ResponseEntity<>(songList, httpHeaders, HttpStatus.OK);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list-top")
    public ResponseEntity<Iterable<SongDTO>> topSongList(@RequestParam(value = "sort", required = false) String sort) {
        Iterable<SongDTO> songList;
        if (sort != null) {
            songList = this.songService.findTop10By(sort);
        } else {
            songList = this.songService.findAll();
        }
        int size = 0;
        if (songList instanceof Collection) {
            size = ((Collection<?>) songList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            this.songService.setLike(songList);
            return new ResponseEntity<>(songList, HttpStatus.OK);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<SongDTO> songDetail(@RequestParam("id") Long id) {
        Optional<SongDTO> song = this.songService.findById(id);
        song.ifPresent(songService::setLike);
        return song.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/search", params = "name")
    public ResponseEntity<Iterable<SongDTO>> songListByName(@RequestParam("name") String name) {
        Iterable<SongDTO> songList = this.songService.findAllByTitleContaining(name);
        int listSize = 0;
        if (songList instanceof Collection) {
            listSize = ((Collection<?>) songList).size();
        }
        if (listSize == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/search", params = "tag")
    public ResponseEntity<Page<SongDTO>> songListByTag(@RequestParam("tag") String tag, Pageable pageable) {
        Page<SongDTO> songList = this.songService.findAllByTag_Name(tag, pageable);
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_SONG')")
    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<Void> editSong(@RequestPart("song") SongDTO song, @RequestParam("id") Long id,
                                         @RequestPart(value = "audio", required = false)
                                                 MultipartFile multipartFile) throws IOException {
        Optional<SongDTO> oldSong = this.songService.findById(id);
        if (oldSong.isPresent()) {
            if (multipartFile != null) {
                String fileDownloadUri = this.storageService.upload(multipartFile, oldSong.get());
                song.setUrl(fileDownloadUri);
            }
            this.songService.setFields(oldSong.get(), song);
            this.songService.save(oldSong.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('DELETE_SONG')")
    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteSong(@RequestParam("id") Long id) {
        this.songService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-song")
    public ResponseEntity<Page<SongDTO>> mySongList(Pageable pageable) {
        Page<SongDTO> mySongList = this.songService
                .findAllByUsersContains(userService.getCurrentUser(), pageable);
        if (mySongList.getTotalElements() > 0) {
            return new ResponseEntity<>(mySongList, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"like", "song-id"})
    public ResponseEntity<Void> likeSong(@RequestParam("song-id") Long id) {
        this.likeService.like(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"unlike", "song-id"})
    public ResponseEntity<Void> dislikeSong(@RequestParam("song-id") Long id) {
        this.likeService.unlike(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/uploaded/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SongDTO>> userSongList(Pageable pageable) {
        UserDTO currentUser = this.userService.getCurrentUser();
        Page<SongDTO> userSongList = this.songService.findAllByUploader_Id(currentUser.getId(), pageable);
        boolean isEmpty = userSongList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(userSongList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(params = {"listen", "song-id"})
    public ResponseEntity<Void> listenToSong(@RequestParam("song-id") Long id) {
        Optional<SongDTO> song = this.songService.findById(id);
        if (song.isPresent()) {
            long currentListeningFrequency = song.get().getListeningFrequency();
            song.get().setListeningFrequency(++currentListeningFrequency);
            this.songService.save(song.get());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"comment", "song-id"})
    public ResponseEntity<Void> commentOnSong(@Valid @RequestBody CommentDTO comment,
                                              @RequestParam("song-id") Long songId) {
        if (this.commentService.save(comment, songId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(params = {"comment", "comment-id"})
    public ResponseEntity<Void> deleteCommentOnSong(@RequestParam("comment-id") Long id) {
        Optional<CommentDTO> comment = this.commentService.findById(id);
        if (comment.isPresent() && comment.get().getUserInfo().getId()
                .equals(userService.getCurrentUser().getId())) {
            this.commentService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
