package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "song_rating")
public class SongRating {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Size(max = 5)
    Integer rating;

    @JsonManagedReference(value = "song-rating")
    @ManyToOne(fetch = FetchType.LAZY)
    Song song;

    @JsonManagedReference(value = "user-song_rating")
    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public SongRating() {
    }

    public SongRating(@Size(max = 5) Integer rating) {
        this.rating = rating;
    }

    public SongRating(@Size(max = 5) Integer rating, Song song, User user) {
        this.rating = rating;
        this.song = song;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SongRating{" +
                "id=" + id +
                ", rating=" + rating +
                ", song=" + song +
                ", user=" + user +
                '}';
    }
}
