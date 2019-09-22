package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "genre")
public class Genre {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Song> songs;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Album> albums;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "Genre{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", songs=" + songs +
                ", albums=" + albums +
                '}';
    }
}


