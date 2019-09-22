package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "album")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    private Date releaseDate;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_genre",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @JsonManagedReference
    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "artist_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_tag",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @JsonManagedReference("album-mood")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @JsonManagedReference("album-activity")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "song_user",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<User> users;

    public Album() {
    }

    public Album(String name, Date releaseDate, Collection<Genre> genres, Collection<Song> songs) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.songs = songs;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Collection<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Collection<Genre> genres) {
        this.genres = genres;
    }

    public Collection<Song> getSongs() {
        return songs;
    }

    public void setSongs(Collection<Song> songList) {
        this.songs = songList;
    }

    public Collection<Artist> getArtists() {
        return artists;
    }

    public void setArtists(Collection<Artist> artists) {
        this.artists = artists;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                ", songList=" + songs +
                '}';
    }
}
