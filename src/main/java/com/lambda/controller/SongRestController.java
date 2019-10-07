package com.lambda.controller;

import com.lambda.model.entity.Album;
import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Song;
import com.lambda.model.form.AudioUploadForm;
import com.lambda.model.util.UploadResponse;
import com.lambda.service.AlbumService;
import com.lambda.service.ArtistService;
import com.lambda.service.SongService;
import com.lambda.service.impl.AudioStorageService;
import com.lambda.service.impl.DownloadService;
import com.lambda.service.impl.FormConvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/song")
public class SongRestController {
    @Autowired
    SongService songService;

    @Autowired
    AlbumService albumService;

    @Autowired
    ArtistService artistService;

    @Autowired
    private AudioStorageService audioStorageService;

    @Autowired
    private FormConvertService formConvertService;

    @Autowired
    private DownloadService downloadService;

    @PostMapping("/upload")
    public ResponseEntity<Void> createSong(@RequestPart("song") Song song, @RequestPart("audio") MultipartFile file) {
        Collection<Artist> artists = song.getArtists();
        for (Artist artist: artists) {
            artistService.save(artist);
        }
        String fileDownloadUri = audioStorageService.saveToFirebaseStorage(song, file);
        song.setUrl(fileDownloadUri);
        songService.save(song);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @GetMapping("/download/{fileName:.+}")
//    public ResponseEntity<Resource> downloadAudio(@PathVariable String fileName, HttpServletRequest request) {
//        return downloadService.generateUrl(fileName, request, audioStorageService);
//    }

    @GetMapping("/list")
    public ResponseEntity<Page<Song>> songList(Pageable pageable) {
        Page<Song> songList = songService.findAll(pageable);
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @GetMapping(value = "/detail", params = {"id"})
    public ResponseEntity<Song> songDetail(@RequestParam("id") Long id) {
        Optional<Song> song = songService.findById(id);
        return song.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/search", params = "name")
    public ResponseEntity<Iterable<Song>> songListByName(@RequestParam("name") String name) {
        Iterable<Song> songList = songService.findAllByNameContaining(name);
        int listSize = 0;
        if (songList instanceof Collection) {
            listSize = ((Collection<?>) songList).size();
        }
        if (listSize==0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }


    @GetMapping(value = "/search", params = "tag")
    public ResponseEntity<Page<Song>> songListByTag(@RequestParam("tag") String tag, Pageable pageable) {
        Page<Song> songList = songService.findAllByTags_Name(tag, pageable);
        if (songList.getTotalElements()==0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<String> editSong(@RequestBody AudioUploadForm audioUploadForm, @RequestParam("id") Long id) {
        Song song = formConvertService.convertToSong(audioUploadForm);
        song.setId(id);
        songService.save(song);
        return new ResponseEntity<>("Song updated successfully!", HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<String> deleteSong(@RequestParam("id") Long id) {
        Boolean result = songService.deleteById(id);
        if (result) {
            return new ResponseEntity<>("Song removed successfully", HttpStatus.OK);
        } else return new ResponseEntity<>("Song removed but media file was not found on server", HttpStatus.NOT_FOUND);
    }


}
