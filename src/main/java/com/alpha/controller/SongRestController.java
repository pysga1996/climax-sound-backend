package com.alpha.controller;

import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongUploadForm;
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

@Log4j2
@RestController
@RequestMapping("/api/song")
public class SongRestController {

    private final SongService songService;

    private final UserService userService;

    private final LikeService likeService;

    private final CommentService commentService;

    private final FormConvertService formConvertService;

    @Autowired
    public SongRestController(SongService songService, LikeService likeService,
        CommentService commentService,
        FormConvertService formConvertService) {
        this.songService = songService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.formConvertService = formConvertService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<SongDTO> uploadSong(
        @Valid @RequestPart("song") SongUploadForm songUploadForm,
        @RequestPart("audio") MultipartFile file) throws IOException {
        SongDTO song = formConvertService.convertSongUploadFormToSong(songUploadForm);
        song = this.songService.uploadAndSaveSong(file, song);
        log.info("Upload song successfully!");
        return new ResponseEntity<>(song, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<SongDTO>> songList(@PageableDefault() Pageable pageable) {
        Page<SongDTO> songList = this.songService.findAll(pageable);
        return new ResponseEntity<>(songList, HttpStatus.OK);
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

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<SongDTO> editSong(@PathVariable("id") Long id,
        @RequestPart("song") SongDTO song,
        @RequestPart(value = "audio", required = false)
            MultipartFile multipartFile) throws IOException {
        SongDTO updatedSong = this.songService.update(id, song, multipartFile);
        return new ResponseEntity<>(updatedSong, HttpStatus.OK);
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
                .findAllByUsersContains(pageable);
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
    @PatchMapping(value = "/like")
    public ResponseEntity<Void> likeSong(@RequestBody LikeSongDTO likeSongDTO) {
        this.likeService.like(likeSongDTO.getSongId(), LikeConfig.SONG, likeSongDTO.getIsLiked());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/like-map")
    public ResponseEntity<Map<Long, Boolean>> songLikeMap(
        @RequestBody Map<Long, Boolean> songLikeMap) {
        Map<Long, Boolean> patchedSongLikeMap = this.songService.getUserSongLikeMap(songLikeMap);
        return new ResponseEntity<>(patchedSongLikeMap, HttpStatus.OK);
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
    public ResponseEntity<CommentDTO> commentOnSong(@Valid @RequestBody CommentDTO comment,
        @RequestParam("song-id") Long songId) {
        CommentDTO songDTO = this.commentService.save(comment, songId);
        return new ResponseEntity<>(songDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(params = {"comment", "comment-id"})
    public ResponseEntity<Void> deleteCommentOnSong(@RequestParam("comment-id") Long id) {
        Optional<CommentDTO> comment = this.commentService.findById(id);
        if (comment.isPresent() && comment.get().getUserInfo().getUsername()
                .equals(this.userService.getCurrentUser().getName())) {
            this.commentService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
