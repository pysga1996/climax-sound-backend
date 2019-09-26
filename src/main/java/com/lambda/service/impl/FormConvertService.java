package com.lambda.service.impl;

import com.lambda.model.*;
import com.lambda.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Service
public class FormConvertService {
    @Autowired
    SongService songService;

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

    public Song convertToSong(AudioUploadForm audioUploadForm) {
        String songName = audioUploadForm.getSongName();
        Date releaseDate = audioUploadForm.getPublishDate();
        if (songService.findByName(songName)!=null) return null;
        Song song = new Song(songName, releaseDate);

        String[] artistsString = audioUploadForm.getGenres().split(",");
        Collection<Artist> artistsList = new HashSet<>();
        for (String artistString: artistsString) {
            Artist checkedArtist = artistService.findByName(artistString);
            if (checkedArtist == null) {
                Artist artist = new Artist(artistString.trim());
                artistService.save(artist);
                artistsList.add(artist);
            } else {
                artistsList.add(checkedArtist);
            }
        }
        song.setArtists(artistsList);

        String[] genresString = audioUploadForm.getGenres().split(",");
        Collection<Genre> genreList = new HashSet<>();
        for (String genreString: genresString) {
            Genre checkedGenre = genreService.findByName(genreString);
            if (checkedGenre == null) {
                Genre genre = new Genre(genreString.toLowerCase().trim());
                genreService.save(genre);
                genreList.add(genre);
            } else {
                genreList.add(checkedGenre);
            }
        }
        song.setGenres(genreList);

        String[] tagsString = audioUploadForm.getTags().split(",");
        Collection<Tag> tagList = new HashSet<>();
        for (String tagString: tagsString) {
            Tag checkedTag = tagService.findByName(tagString);
            if (checkedTag == null) {
                Tag tag = new Tag(tagString.toLowerCase().trim());
                tagService.save(tag);
                tagList.add(tag);
            } else {
                tagList.add(checkedTag);
            }
        }
        song.setTags(tagList);

        Mood checkedMood = moodService.findByName(audioUploadForm.getMood().toLowerCase().trim());
        if (checkedMood == null) {
            Mood mood = new Mood(audioUploadForm.getMood().toLowerCase().trim());
            moodService.save(mood);
            song.setMood(mood);
        } else {
            song.setMood(checkedMood);
        }

        Activity checkedActivity = activityService.findByName(audioUploadForm.getMood().toLowerCase().trim());
        if (checkedActivity == null) {
            Activity activity = new Activity(audioUploadForm.getActivity().toLowerCase().trim());
            activityService.save(activity);
            song.setActivity(activity);
        } else {
            song.setActivity(checkedActivity);
        }
        return song;
    }
}
