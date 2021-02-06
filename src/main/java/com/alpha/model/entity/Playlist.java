package com.alpha.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.alpha.model.dto.UserDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"user", "songs"}, allowGetters = true)
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //    @Column(columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    @NotBlank
    private String title;

    @Column
    private Long userId;

    @Transient
    private UserDTO user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(
                    name = "playlist_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;
}
