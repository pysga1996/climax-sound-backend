package com.alpha.controller;

import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.LikeSongDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.model.dto.SongUploadForm;
import com.alpha.service.CommentService;
import com.alpha.service.LikeService;
import com.alpha.service.LikeService.LikeConfig;
import com.alpha.service.SongService;
import com.alpha.service.impl.FormConvertService;
import java.io.IOException;
import java.util.Map;
import javax.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/api/song")
public class SongRestController {

    private final SongService songService;

    private final LikeService likeService;

    private final CommentService commentService;

    private final FormConvertService formConvertService;

    @Autowired
    public SongRestController(SongService songService, LikeService likeService,
        CommentService commentService,
        FormConvertService formConvertService) {
        this.songService = songService;
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
    @GetMapping(value = "/search")
    public ResponseEntity<Page<SongDTO>> search(@ModelAttribute SongSearchDTO songSearchDTO,
        Pageable pageable) {
        Page<SongDTO> songList = this.songService.findAllByConditions(pageable, songSearchDTO);
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<SongDTO> songDetail(@PathVariable("id") Long id) {
        SongDTO song = this.songService.findById(id);
        return new ResponseEntity<>(song, HttpStatus.OK);
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

    @DeleteMapping(value = "/delete/{id}", params = "id")
    public ResponseEntity<Void> deleteSong(@PathVariable("id") Long id) {
        this.songService.deleteById(id);
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
    @PatchMapping("/listen")
    public ResponseEntity<Void> listenToSong(@RequestBody Long id) {
        this.songService.listenToSong(id);
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
        this.commentService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
