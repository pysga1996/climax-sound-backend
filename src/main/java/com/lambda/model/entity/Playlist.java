package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "playlist")
@JsonIgnoreProperties(value = {"user", "songs"}, allowGetters = true)
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(columnDefinition = "VARCHAR(255) UNIQUE COLLATE utf8mb4_bin")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("user-playlist")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference("playlist-song")
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(
                    name = "playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    public Playlist() {
    }

    public Playlist(@NotBlank String name) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<Song> getSongs() {
        return songs;
    }

    public void setSongs(Collection<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
