package com.alpha.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.util.MediaObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonIgnoreProperties(value = {"users", "coverBlobString", "coverUrl"}, allowGetters = true)
public class Album extends MediaObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String coverUrl;

    private String coverBlobString;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "album_genre",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "album_song",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "album_artist",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "artist_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "album_tag",
            joinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @Transient
    private UserDTO uploader;

    @Transient
    private Collection<UserDTO> users;

    public Album(String title, Date releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
