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

/**
 * @author thanhvt
 * @created 01/09/2021 - 7:29 CH
 * @project vengeance
 * @since 1.0
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_favorite_artists")
public class UserFavoriteArtist {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "artistId", column = @Column(name = "artist_id")),
        @AttributeOverride(name = "username", column = @Column(name = "username")),
    })
    private UserFavoriteArtistId userFavoriteSongId;

    @Column(name = "liked")
    private boolean liked;

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class UserFavoriteArtistId implements Serializable {

        private String username;

        private Long artistId;
    }
}
