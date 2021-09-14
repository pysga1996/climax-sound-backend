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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author thanhvt
 * @created 01/09/2021 - 7:25 CH
 * @project vengeance
 * @since 1.0
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_favorite_albums")
public class UserFavoriteAlbum {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "albumId", column = @Column(name = "album_id")),
        @AttributeOverride(name = "username", column = @Column(name = "username")),
    })
    private UserFavoritesAlbumId userFavoritesAlbumId;

    @Column(name = "liked")
    private boolean liked;

    @Column(name = "listening_count")
    private Long listeningCount;

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(of = {"username", "albumId"})
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class UserFavoritesAlbumId implements Serializable {

        private String username;

        private Long albumId;
    }
}
