package com.alpha.model.entity;

import java.io.Serializable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_favorite_songs")
public class UserFavoriteSong {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "songId", column = @Column(name = "song_id")),
        @AttributeOverride(name = "username", column = @Column(name = "username")),
    })
    private UserFavoriteSongId userFavoriteSongId;

    @Column(name = "liked")
    private boolean liked;

    @Column(name = "listening_count")
    private Long listeningCount;

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class UserFavoriteSongId implements Serializable {

        private String username;

        private Long songId;
    }

}
