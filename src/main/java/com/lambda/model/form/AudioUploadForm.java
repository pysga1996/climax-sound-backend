package com.lambda.model.form;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class AudioUploadForm implements MediaForm {
    @NotBlank
    private String name;

    @NotBlank
    private String artists;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @Pattern(regexp = "(^[a-z0-9_-]{3,16}$|^$)")
    private String album;

    private String genres;

    private String tags;

    private String mood;

    private String activity;

    public AudioUploadForm() {
    }

    public AudioUploadForm(@NotBlank String name, @NotBlank String artists, Date releaseDate, String album, String genres, String tags, String mood, String activity) {
        this.name = name;
        this.artists = artists;
        this.releaseDate = releaseDate;
        this.album = album;
        this.genres = genres;
        this.tags = tags;
        this.mood = mood;
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "AudioUploadForm{" +
                "name='" + name + '\'' +
                ", artists='" + artists + '\'' +
                ", releaseDate=" + releaseDate +
                ", tags='" + tags + '\'' +
                ", mood='" + mood + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }
}
