package com.lambda.model.form;

import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotBlank;
import java.util.Date;

public class AlbumForm implements MediaForm {
    @NotBlank
    private String name;

    @NotBlank
    private String artists;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String genres;

    private String tags;

    private String country;

    private String theme;

    public AlbumForm() {
    }

    public AlbumForm(String name, Date releaseDate, String artists, String genres, String tags, String country, String theme) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.artists = artists;
        this.genres = genres;
        this.tags = tags;
        this.country = country;
        this.theme = theme;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
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
