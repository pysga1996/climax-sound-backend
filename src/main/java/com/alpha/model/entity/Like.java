package com.alpha.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_favorite_songs")
@Data
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "song_id")
    private Long songId;

    @Transient
    private Long userId;

    public Like() {
    }

    public Like(Long songId, Long userId) {
        this.songId = songId;
        this.userId = userId;
    }
}
