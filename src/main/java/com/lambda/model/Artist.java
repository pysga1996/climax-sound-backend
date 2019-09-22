package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "artist")
public class Artist {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    private Date birthDate;

    private String biography;

    @JsonBackReference
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonBackReference
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    public Artist() {
    }

    public Artist(@NotNull String name, Date birthDate, String biography) {
        this.name = name;
        this.birthDate = birthDate;
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Collection<Song> getSongs() {
        return songs;
    }

    public void setSongs(Collection<Song> songs) {
        this.songs = songs;
    }

    public Collection<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Collection<Album> albums) {
        this.albums = albums;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
