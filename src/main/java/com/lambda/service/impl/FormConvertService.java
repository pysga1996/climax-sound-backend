package com.lambda.service.impl;

import com.lambda.model.entity.*;
import com.lambda.model.util.AlbumForm;
import com.lambda.model.util.AudioUploadForm;
import com.lambda.model.util.MediaForm;
import com.lambda.model.util.MediaObject;
import com.lambda.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FormConvertService {
    @Autowired
    SongService songService;

    @Autowired
    AlbumService albumService;

    @Autowired
    ArtistService artistService;

    @Autowired
    GenreService genreService;

    @Autowired
    TagService tagService;

    @Autowired
    MoodService moodService;

    @Autowired
    ActivityService activityService;

    private Collection<Artist> convertStringToArtistList(String string) {
        String[] artistsString = string.split(",");
        Collection<Artist> artistList = new HashSet<>();
        for (String artistString: artistsString) {
            if (!artistString.trim().isEmpty()) {
                Artist checkedArtist = artistService.findByName(artistString);
                if (checkedArtist == null) {
                    Artist artist = new Artist(artistString.trim());
                    artistService.save(artist);
                    artistList.add(artist);
                } else {
                    artistList.add(checkedArtist);
                }
            }
        }
        if (artistList.isEmpty()) return null;
        return artistList;
    }

    private Collection<Genre> convertStringToGenreList(String string) {
        String[] genresString = string.split(",");
        Collection<Genre> genreList = new HashSet<>();
        for (String genreString: genresString) {
            if (!genreString.trim().isEmpty()) {
                Genre checkedGenre = genreService.findByName(genreString);
                if (checkedGenre == null) {
                    Genre genre = new Genre(genreString.toLowerCase().trim());
                    genreService.save(genre);
                    genreList.add(genre);
                } else {
                    genreList.add(checkedGenre);
                }
            }
        }
        if (genreList.isEmpty()) return null;
        return genreList;
    }

    private Collection<Tag> convertStringToTagList(String string) {
        String[] tagsString = string.split(",");
        Collection<Tag> tagList = new HashSet<>();
        for (String tagString: tagsString) {
            if (!tagString.trim().isEmpty()) {
                Tag checkedTag = tagService.findByName(tagString);
                if (checkedTag == null) {
                    Tag tag = new Tag(tagString.toLowerCase().trim());
                    tagService.save(tag);
                    tagList.add(tag);
                } else {
                    tagList.add(checkedTag);
                }
            }
        }
        if (tagList.isEmpty()) return null;
        return tagList;
    }

    private Mood convertStringToMood(String string) {
        Mood checkedMood = moodService.findByName(string);
        if (checkedMood == null && !string.isEmpty()) {
            Mood mood = new Mood(string);
            moodService.save(mood);
            return mood;
        }
        return null;
    }

    private Activity convertToActivity(String string) {
        Activity checkedActivity = activityService.findByName(string);
        if (checkedActivity == null && !string.isEmpty()) {
            Activity activity = new Activity(string);
            activityService.save(activity);
            return activity;
        }
        return null;
    }

    private Boolean checkSongExist(AudioUploadForm audioUploadForm) {
        Song checkedSong = songService.findByName(audioUploadForm.getName());
        if (checkedSong != null) {
            return compareTwoArtistSet(checkedSong.getArtists(), audioUploadForm.getArtists().split(","));
        }
        return false;
    }

    private Boolean checkAlbumExist(AlbumForm albumForm) {
        Album checkedAlbum = albumService.findByName(albumForm.getName());
        if (checkedAlbum != null) {
            return compareTwoArtistSet(checkedAlbum.getArtists(), albumForm.getArtists().split(","));
        }
        return false;
    }

    private boolean compareTwoArtistSet(Collection<Artist> checkedArtistCollection, String[] artistStringArray) {
        Set<Artist> checkedArtistSet = new HashSet<>(checkedArtistCollection);
        Set<String> checkedArtistStringSet = new HashSet<>();
        for (Artist artist: checkedArtistSet) {
            checkedArtistStringSet.add(artist.getName());
        }
        Set<String> artistStringSet = new HashSet<>(Arrays.asList(artistStringArray));
        return checkedArtistStringSet.equals(artistStringSet);
    }

    public Song convertToSong(AudioUploadForm audioUploadForm) {
        String songName = audioUploadForm.getName();
        Date releaseDate = audioUploadForm.getReleaseDate();
        if (checkSongExist(audioUploadForm)) return null;
        Song song = new Song(songName, releaseDate);
        if (audioUploadForm.getAlbum() != null  ) {
            Album album = albumService.findByName(audioUploadForm.getAlbum().trim());
            if (album != null) {
                song.setAlbum(album);
            }
        }

        setFields(song, audioUploadForm);
        return song;
    }

    public Album convertToAlbum(AlbumForm albumForm) {
        String songName = albumForm.getName();
        Date releaseDate = albumForm.getReleaseDate();
        if (checkAlbumExist(albumForm)) return null;
        Album album = new Album(songName, releaseDate);
        setFields(album, albumForm);
        return album;
    }

    private void setFields(MediaObject mediaObject, MediaForm mediaForm) {
        Collection<Artist> artistsList = convertStringToArtistList(mediaForm.getArtists());
        mediaObject.setArtists(artistsList);
        Collection<Genre> genreList = convertStringToGenreList(mediaForm.getGenres());
        mediaObject.setGenres(genreList);
        Collection<Tag> tagList = convertStringToTagList(mediaForm.getTags());
        mediaObject.setTags(tagList);
        Mood mood = convertStringToMood(mediaForm.getMood().trim());
        mediaObject.setMood(mood);
        Activity activity = convertToActivity(mediaForm.getMood().toLowerCase().trim());
        mediaObject.setActivity(activity);
    }
}
