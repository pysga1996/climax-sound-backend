package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lambda.model.util.MediaObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "song")
@JsonIgnoreProperties(value = {"ratings", "artists", "album", "tags", "genres", "users", "playlists"}, allowGetters = true)
public class Song implements MediaObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    @NotBlank
    private String url;

    @JsonManagedReference(value = "song-rating")
    @OneToMany(mappedBy = "song")
    private Collection<SongRating> ratings;


    private Double displayRating;

    @JsonManagedReference(value = "song-artist")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "artist_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @JsonBackReference(value = "album-song")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @JsonManagedReference(value = "song-tag")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_tag",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @JsonManagedReference("song-genre")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @JsonBackReference("user-song")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_song",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<User> users;

    @JsonBackReference("playlist-song")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Playlist> playlists;

    @JsonManagedReference(value = "song-mood")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @JsonManagedReference(value = "song-activity")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    public Song() {
    }

    public Song(String name, Date releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getUrl() {
        return url;
    }

    public Collection<SongRating> getRatings() {
        return ratings;
    }

    public void setRatings(Collection<SongRating> ratings) {
        this.ratings = ratings;
    }

    public Double getDisplayRating() {
        return displayRating;
    }

    public void setDisplayRating(Double displayRating) {
        this.displayRating = displayRating;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Collection<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Collection<Genre> genres) {
        this.genres = genres;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }

    public Collection<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Collection<Playlist> playlists) {
        this.playlists = playlists;
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

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
