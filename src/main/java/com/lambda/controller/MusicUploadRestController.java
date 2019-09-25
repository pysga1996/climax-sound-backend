package com.lambda.controller;

import com.lambda.model.MusicUploadForm;
import com.lambda.model.Song;
import com.lambda.model.UploadResponse;
import com.lambda.service.SongService;
import com.lambda.service.impl.FormConvertService;
import com.lambda.service.impl.TrackStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class MusicUploadRestController {
    @Autowired
    private SongService songService;

    @Autowired
    private FormConvertService formConvertService;

    @Autowired
    private TrackStorageService trackStorageService;

    @PostMapping(value = "/upload-track")
    public ResponseEntity<UploadResponse> uploadMusic(@RequestPart("musicUploadForm") MusicUploadForm musicUploadForm, @RequestPart(value = "audio", required = false) MultipartFile file) {
        Song song = formConvertService.convertToSong(musicUploadForm);
        if (song == null) return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        String fileName = trackStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download-track/")
                .path(fileName)
                .toUriString();
        song.setUrl(fileDownloadUri);
        songService.save(song);
        return new ResponseEntity<>(new UploadResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize()), HttpStatus.OK);
    }
}
