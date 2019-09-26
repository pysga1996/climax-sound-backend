package com.lambda.controller;

import com.lambda.model.AudioUploadForm;
import com.lambda.model.Song;
import com.lambda.model.UploadResponse;
import com.lambda.service.SongService;
import com.lambda.service.impl.FormConvertService;
import com.lambda.service.impl.AudioStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class AudioUploadRestController {
    @Autowired
    private SongService songService;

    @Autowired
    private FormConvertService formConvertService;

    @Autowired
    private AudioStorageService audioStorageService;

    @PostMapping(value = "/upload-audio")
    public ResponseEntity<UploadResponse> uploadAudio(@RequestPart("audioUploadForm") AudioUploadForm audioUploadForm, @RequestPart(value = "audio", required = false) MultipartFile file) {
        Song song = formConvertService.convertToSong(audioUploadForm);
        if (song == null) return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        String fileName = audioStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download-audio/")
                .path(fileName)
                .toUriString();
        song.setUrl(fileDownloadUri);
        songService.save(song);
        return new ResponseEntity<>(new UploadResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize()), HttpStatus.OK);
    }
}
