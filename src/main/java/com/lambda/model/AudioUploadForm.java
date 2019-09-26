package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class AudioUploadForm {
    @NotBlank
    private String songName;

    @NotBlank
    private String artistName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date publishDate;

    private String genres;

    private String tags;

    private String mood;

    private String activity;

    public AudioUploadForm() {
    }

    public AudioUploadForm(String songName, String artistName, Date publishDate, String genres, String tags, String mood, String activity) {
        this.songName = songName;
        this.artistName = artistName;
        this.publishDate = publishDate;
        this.genres = genres;
        this.tags = tags;
        this.mood = mood;
        this.activity = activity;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
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
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", publishDate=" + publishDate +
                ", tags='" + tags + '\'' +
                ", mood='" + mood + '\'' +
                ", activity='" + activity + '\'' +
                '}';
    }
}
