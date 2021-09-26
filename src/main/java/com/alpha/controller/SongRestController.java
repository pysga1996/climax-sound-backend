package com.alpha.controller;

import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongDTO.SongAdditionalInfoDTO;
import com.alpha.model.dto.SongSearchDTO;
import com.alpha.service.SongService;
import java.io.IOException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequestMapping("/api/song")
public class SongRestController {

    private final SongService songService;

    @Autowired
    public SongRestController(SongService songService) {
        this.songService = songService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<SongDTO> uploadSong(
        @Valid @RequestPart("song") SongDTO songDTO,
        @RequestPart("audio") MultipartFile file) {
        SongDTO createdSongDTO = this.songService.uploadAndSaveSong(file, songDTO);
        log.info("Upload song successfully!");
        return new ResponseEntity<>(createdSongDTO, HttpStatus.OK);
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

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/additional-info/{id}")
    public ResponseEntity<SongAdditionalInfoDTO> songAdditionalInfo(@PathVariable("id") Long id) {
        SongAdditionalInfoDTO additionalInfoDTO = this.songService.findAdditionalInfoById(id);
        return new ResponseEntity<>(additionalInfoDTO, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(value = "/edit/{id}")
    public ResponseEntity<SongDTO> editSong(@PathVariable("id") Long id,
        @RequestPart("song") SongDTO songDTO,
        @RequestPart(value = "audio", required = false)
            MultipartFile multipartFile) throws IOException {
        SongDTO updatedSong = this.songService.update(id, songDTO, multipartFile);
        return new ResponseEntity<>(updatedSong, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}", params = "id")
    public ResponseEntity<Void> deleteSong(@PathVariable("id") Long id) {
        this.songService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
