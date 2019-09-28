package com.lambda.controller;

import com.lambda.model.entity.Song;
import com.lambda.model.util.AudioUploadForm;
import com.lambda.model.util.UploadResponse;
import com.lambda.service.SongService;
import com.lambda.service.impl.AudioStorageService;
import com.lambda.service.impl.DownloadService;
import com.lambda.service.impl.FormConvertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/song")
public class SongRestController {
    @Autowired
    SongService songService;

    @Autowired
    private AudioStorageService audioStorageService;

    @Autowired
    private FormConvertService formConvertService;

    @Autowired
    private DownloadService downloadService;

    @PostMapping(value = "/upload")
    public ResponseEntity<UploadResponse> uploadAudio(@RequestPart("audioUploadForm") AudioUploadForm audioUploadForm, @RequestPart("audio") MultipartFile file) {
        Song song = formConvertService.convertToSong(audioUploadForm);
        if (song == null) return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        String fileName = audioStorageService.storeFile(file, song);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/song/download/")
                .path(fileName)
                .toUriString();
        song.setUrl(fileDownloadUri);
        songService.save(song);
        return new ResponseEntity<>(new UploadResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize()), HttpStatus.OK);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadAudio(@PathVariable String fileName, HttpServletRequest request) {
        return downloadService.generateUrl(fileName, request, audioStorageService);
    }

    @GetMapping(params = "action=list")
    public ResponseEntity<Page<Song>> songList(Pageable pageable) {
        Page<Song> songList = songService.findAll(pageable);
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @GetMapping(params = {"action=detail", "id"})
    public ResponseEntity<Song> songDetail(@RequestParam("id") Long id) {
        Optional<Song> song = songService.findById(id);
        return song.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @GetMapping(params = "tag")
    public ResponseEntity<Page<Song>> songListByTag(@RequestParam("tag") String tag, Pageable pageable) {
        Page<Song> songList = songService.findAllByTags_Name(tag, pageable);
        if (songList.getTotalElements()==0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @DeleteMapping(params = "id")
    public ResponseEntity<String> deleteSong(@RequestParam("id") Long id) {
        Boolean result = songService.deleteById(id);
        if (result) {
            return new ResponseEntity<>("Song removed successfully", HttpStatus.OK);
        } else return new ResponseEntity<>("Song removed but media file was not found on server", HttpStatus.NOT_FOUND);
    }


}
