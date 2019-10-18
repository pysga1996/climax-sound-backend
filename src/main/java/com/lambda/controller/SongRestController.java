package com.lambda.controller;

import com.lambda.model.entity.*;
import com.lambda.service.*;
import com.lambda.service.impl.AudioStorageService;
import com.lambda.service.impl.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
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
    UserService userService;

    @Autowired
    PeopleWhoLikedService peopleWhoLikedService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    private AudioStorageService audioStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadSong(@RequestPart("song") Song song, @RequestPart("audio") MultipartFile file, @RequestParam(value = "album-id", required = false) Long id) {
        try {
            Song songToSave = songService.save(song);
            String fileDownloadUri = audioStorageService.saveToFirebaseStorage(songToSave, file);
            songToSave.setUrl(fileDownloadUri);
            songToSave.setUploader(userDetailService.getCurrentUser());
//            if (id != null) {
//                Optional<Album> album = albumService.findById(id);
//                Collection<Album> albumList = songToSave.getAlbums();
//                if (album.isPresent()) {
//                    if (albumList == null) {
//                        albumList = new ArrayList<>();
//                    }
//                    albumList.add(album.get());
//                    songToSave.setAlbums(albumList);
//                    songService.save(songToSave);
//                    albumService.save(album.get());
//                }
//            }
            if (id != null) {
                Optional<Album> album = albumService.findById(id);
                if (album.isPresent()) {
                    Collection<Song> songList = album.get().getSongs();
                    if (songList == null) {
                        songList = new ArrayList<>();
                    }
                    songList.add(song);
                    album.get().setSongs(songList);
                    albumService.save(album.get());
                }
            }
            songService.save(songToSave);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            if (song.getId() != null) {
                songService.deleteById(song.getId());
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/download/{fileName:.+}")
//    public ResponseEntity<Resource> downloadAudio(@PathVariable String fileName, HttpServletRequest request) {
//        return downloadService.generateUrl(fileName, request, audioStorageService);
//    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list")
    public ResponseEntity<Page<Song>> songList(@PageableDefault(size = 10) Pageable pageable, @RequestParam(value = "sort", required = false) String sort) {
        Page<Song> songList = songService.findAll(pageable, sort);
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            songService.setLike(songList);
            return new ResponseEntity<>(songList, HttpStatus.OK);
        }
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = "/list-top")
    public ResponseEntity<Iterable<Song>> topSongList(@RequestParam(value = "sort", required = false) String sort) {
        Iterable<Song> songList = songService.findTop10By(sort);
        int size = 0;
        if (songList instanceof Collection) {
            size = ((Collection<?>) songList).size();
        }
        if (size == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            songService.setLike(songList);
            return new ResponseEntity<>(songList, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/detail", params = "id")
    public ResponseEntity<Song> songDetail(@RequestParam("id") Long id) {
        Optional<Song> song = songService.findById(id);
        return song.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/search", params = "name")
    public ResponseEntity<Iterable<Song>> songListByName(@RequestParam("name") String name) {
        Iterable<Song> songList = songService.findAllByTitleContaining(name);
        int listSize = 0;
        if (songList instanceof Collection) {
            listSize = ((Collection<?>) songList).size();
        }
        if (listSize == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }


    @GetMapping(value = "/search", params = "tag")
    public ResponseEntity<Page<Song>> songListByTag(@RequestParam("tag") String tag, Pageable pageable) {
        Page<Song> songList = songService.findAllByTag_Name(tag, pageable);
        if (songList.getTotalElements() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(songList, HttpStatus.OK);
    }

    @PutMapping(value = "/edit", params = "id")
    public ResponseEntity<Void> editSong(@RequestPart("song") Song song, @RequestParam("id") Long id, @RequestPart(value = "audio",required = false) MultipartFile multipartFile) {
        Optional<Song> oldSong = songService.findById(id);
        if(oldSong.isPresent()){
            if (multipartFile != null) {
                String fileDownloadUri = audioStorageService.saveToFirebaseStorage(oldSong.get(), multipartFile);
                song.setUrl(fileDownloadUri);
            }
            songService.setFields(oldSong.get(),song);
            songService.save(oldSong.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete", params = "id")
    public ResponseEntity<Void> deleteSong(@RequestParam("id") Long id) {
      songService.deleteById(id);
      return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my-song")
    public ResponseEntity<Page<Song>> mySongList(Pageable pageable) {
        Page<Song> mySongList = songService.findAllByUploader_Id(userDetailService.getCurrentUser().getId(), pageable);
        if (mySongList.getTotalElements() > 0) {
            return new ResponseEntity<>(mySongList, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"like", "song-id"})
    public ResponseEntity<Void> likeSong(@RequestParam("song-id") Long id){
        peopleWhoLikedService.like(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"unlike", "song-id"})
    public ResponseEntity<Void> dislikeSong(@RequestParam("song-id") Long id){
        peopleWhoLikedService.unlike(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/uploaded/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Song>> userSongList(Pageable pageable) {
        User currentUser = userDetailService.getCurrentUser();
        Page<Song> userSongList = songService.findAllByUploader_Id(currentUser.getId(), pageable);
        boolean isEmpty = userSongList.getTotalElements() == 0;
        if (isEmpty) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>(userSongList, HttpStatus.OK);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(params = {"listen", "song-id"})
    public ResponseEntity<Void> listenToSong(@RequestParam("song-id") Long id) {
        Optional<Song> song = songService.findById(id);
        if (song.isPresent()) {
            long currentListeningFrequency = song.get().getListeningFrequency();
            song.get().setListeningFrequency(++currentListeningFrequency);
            songService.save(song.get());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(params = {"comment", "song-id"})
    public ResponseEntity<Void> commentOnSong(@Valid @RequestBody Comment comment, @RequestParam("song-id") Long id) {
        Optional<Song> song = songService.findById(id);
        if (song.isPresent()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            User currentUser = userDetailService.getCurrentUser();
            comment.setLocalDateTime(localDateTime);
            comment.setSong(song.get());
            comment.setUser(currentUser);
            commentService.save(comment);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(params = {"comment-id"})
    public ResponseEntity<Void> deleteCommentOnSong(@RequestParam("comment-id") Long id) {
        Optional<Comment> comment = commentService.findById(id);
        comment.ifPresent(value -> commentService.deleteById(value.getId()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @PreAuthorize("permitAll()")
//    @GetMapping
//    public ResponseEntity<> getCommentList(@RequestParam("song-id") Long id) {
//
//    }
}
