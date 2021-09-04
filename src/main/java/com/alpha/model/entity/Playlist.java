package com.alpha.model.entity;

import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "playlist")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_id_gen")
    @SequenceGenerator(name = "playlist_id_gen", sequenceName = "playlist_id_seq", allocationSize = 1)
    @Column(name = "id")
    @ToString.Include
    private Long id;

    //    @Column(columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    @NotBlank
    @Column(name = "title")
    @ToString.Include
    private String title;

    @Column(name = "username")
    @ToString.Include
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
