package com.lambda.controller;

import com.lambda.model.Song;
import com.lambda.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/song")
public class SongRestController {
    @Autowired
    SongService songService;

    @GetMapping(params = "tagName")
    public ResponseEntity<Page<Song>> songListByTag(@RequestParam String tagName, Pageable pageable) {
        Page<Song> songList = songService.findAllByTags_Name(tagName, pageable);
        if (songList.getTotalElements()==0) {
            return new ResponseEntity<Page<Song>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Page<Song>>(songList, HttpStatus.OK);
    }
}
