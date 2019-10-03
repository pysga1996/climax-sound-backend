package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(max = 5)
    private Integer rating;

    @JsonManagedReference(value = "song-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private Song song;

    @JsonManagedReference(value = "user-comment")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Comment() {
    }

    public Comment(@Size(max = 5) Integer rating) {
        this.rating = rating;
    }

    public Comment(@Size(max = 5) Integer rating, Song song, User user) {
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
        return "Comment{" +
                "id=" + id +
                ", rating=" + rating +
                ", song=" + song +
                ", user=" + user +
                '}';
    }
}
