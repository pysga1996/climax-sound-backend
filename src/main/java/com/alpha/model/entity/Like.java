package com.alpha.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_favorite_songs")
public class Like {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "songId", column = @Column(name = "song_id")),
            @AttributeOverride(name = "username", column = @Column(name = "username")),
    })
    private LikeId likeId;

}
