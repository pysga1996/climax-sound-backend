package com.lambda.model;

import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Table(name = "tag")
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @JsonBackReference(value = "song-tag")
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonBackReference(value = "album-tag")
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    public Tag() {
    }

    public Tag(@NotNull String name) {
        this.name = name;
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
        return "TagRepository{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
