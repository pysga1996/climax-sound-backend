package com.lambda.model.form;

import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class AlbumForm implements MediaForm {
    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @NotBlank
    private String artists;

    private String genres;

    private String tags;

    private String mood;

    private String activity;

    public AlbumForm() {
    }

    public AlbumForm(String name, Date releaseDate, String artists, String genres, String tags, String mood, String activity) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.artists = artists;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
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
        return "AlbumForm{" +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                ", artists='" + artists + '\'' +
                '}';
    }
}
