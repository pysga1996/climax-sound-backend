package com.lambda.service.impl;

import com.lambda.model.entity.Artist;
import com.lambda.model.entity.Like;
import com.lambda.model.entity.Song;
import com.lambda.model.entity.User;
import com.lambda.repository.LikeRepository;
import com.lambda.repository.SongRepository;
import com.lambda.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    SongRepository songRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    UserDetailServiceImpl userDetailService;

    @Autowired
    AudioStorageService audioStorageService;

    @Override
    public Page<Song> findAll(Pageable pageable, String sort) {
        if (sort != null && sort.equals("releaseDate")) {
            return songRepository.findAllByOrderByReleaseDateDesc(pageable);
        } else if (sort != null && sort.equals("listeningFrequency")) {
            return songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
        } else if (sort != null && sort.equals("likesCount")) {
            return songRepository.findAllByOrderByUsers_Size(pageable);
        } else {
            return songRepository.findAll(pageable);
        }
    }

    @Override
    public Iterable<Song> findAll() {
        return songRepository.findAll();
    }

    @Override
    public Iterable<Song> findTop10By(String sort) {
        if (sort.equals("listeningFrequency")) {
            return songRepository.findFirst10ByOrderByListeningFrequencyDesc();
        } else return songRepository.findFirst10ByOrderByListeningFrequencyDesc();
    }

    @Override
    public Page<Song> findAllByOrderByReleaseDateDesc(Pageable pageable) {
        return songRepository.findAllByOrderByReleaseDateDesc(pageable);
    }

    @Override
    public Page<Song> findAllByOrderByDisplayRatingDesc(Pageable pageable) {
        return songRepository.findAllByOrderByDisplayRatingDesc(pageable);
    }

    @Override
    public Page<Song> findAllByOrderByListeningFrequencyDesc(Pageable pageable) {
        return songRepository.findAllByOrderByListeningFrequencyDesc(pageable);
    }

    @Override
    public Page<Song> findAllByLikesCount(Pageable pageable) {
        return songRepository.findAllByOrderByUsers_Size(pageable);
    }

    @Override
    public Page<Song> findAllByUploader_Id(Long id, Pageable pageable) {
        return songRepository.findAllByUploader_Id(id, pageable);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public Iterable<Song> findAllByTitle(String title) {
        return songRepository.findAllByTitle(title);
    }

    @Override
    public Iterable<Song> findAllByTitleContaining(String name) {
        return songRepository.findAllByTitleContaining(name);
    }

    @Override
    public Page<Song> findAllByTitleContaining(String name, Pageable pageable) {
        return songRepository.findAllByTitleContaining(name, pageable);
    }

    @Override
    public Page<Song> findAllByArtistsContains(Artist artist, Pageable pageable) {
        return songRepository.findAllByArtistsContains(artist, pageable);
    }

    @Override
    public Page<Song> findAllByUsersContains(User user, Pageable pageable) {
        Page<Song> songList = songRepository.findAllByUsersContains(user, pageable);
        setLike(songList);
        return songList;
    }

    @Override
    public Page<Song> findAllByTag_Name(String name, Pageable pageable) {
        return songRepository.findAllByTag_Name(name, pageable);
    }

    @Override
    public Iterable<Song> findAllByAlbum_Id(Long id) {
        return songRepository.findAllByAlbum_Id(id);
    }

    @Override
    public Song save(Song song) {
        songRepository.saveAndFlush(song);
        return song;
    }

    @Override
    public void deleteById(Long id) {
        Optional<Song> song = songRepository.findById(id);
        if (song.isPresent()) {
            songRepository.deleteById(id);
//            String fileUrl = song.get().getUrl();
//            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//            return audioStorageService.deleteLocalStorageFile(audioStorageService.audioStorageLocation, filename);
            audioStorageService.deleteFirebaseStorageFile(song.get());
        }
    }

    @Override
    public void deleteAll(Collection<Song> songs) {
        songRepository.deleteAll(songs);
    }
    @Override
    public void setFields(Song oldSongInfo,Song newSongInfo){
        oldSongInfo.setTitle(newSongInfo.getTitle());
        oldSongInfo.setArtists(newSongInfo.getArtists());
        oldSongInfo.setGenres(newSongInfo.getGenres());
        oldSongInfo.setCountry(newSongInfo.getCountry());
        oldSongInfo.setReleaseDate(newSongInfo.getReleaseDate());
        oldSongInfo.setTags(newSongInfo.getTags());
        oldSongInfo.setTheme(newSongInfo.getTheme());
        if(newSongInfo.getUrl()!= null) {
            oldSongInfo.setUrl(newSongInfo.getUrl());
        }
    }

    @Override
    public Page<Song> sortByDate(Pageable pageable) {
        return null;
    }

    @Override
    public boolean hasUserLiked(Long songId) {
        Long userId = userDetailService.getCurrentUser().getId();
        Like like = likeRepository.findBySongIdAndUserId(songId, userId);
        return (like != null);
    }

    @Override
    public void setLike(Song song) {
        if (hasUserLiked(song.getId())) {
            song.setLiked(true);
        } else {
            song.setLiked(false);
        }
    }

    @Override
    public void setLike(Page<Song> songList) {
        for (Song song: songList) {
            if (hasUserLiked(song.getId())) {
                song.setLiked(true);
            } else {
                song.setLiked(false);
            }
        }
    }

    @Override
    public void setLike(Iterable<Song> songList) {
        for (Song song: songList) {
            if (hasUserLiked(song.getId())) {
                song.setLiked(true);
            } else {
                song.setLiked(false);
            }
        }
    }
}
