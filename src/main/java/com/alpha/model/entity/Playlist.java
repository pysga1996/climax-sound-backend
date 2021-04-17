package com.alpha.model.entity;

import com.alpha.model.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "playlist")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_id_gen")
    @SequenceGenerator(name = "playlist_id_gen", sequenceName = "playlist_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    //    @Column(columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "username")
    private String username;

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
