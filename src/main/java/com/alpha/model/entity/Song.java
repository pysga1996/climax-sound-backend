package com.alpha.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.util.MediaObject;
import com.alpha.util.helper.CustomUserJsonSerializer;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"comments", "liked", "albums", "genres", "users", "playlists", "theme", "uploader"}, allowGetters = true, ignoreUnknown = true)
public class Song extends MediaObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    @JsonIgnore
    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String url;

    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY)
    private Collection<Comment> comments;

    @Builder.Default
    @ColumnDefault("0")
    private Long displayRating = 0L;

    @Builder.Default
    @ColumnDefault("0")
    private Long listeningFrequency = 0L;

    private Boolean liked;

    //    @Column(columnDefinition = "LONGTEXT")
    @Column(columnDefinition = "TEXT")
    private String lyric;

    @JsonIgnore
    private String blobString;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "artist_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs", cascade = CascadeType.ALL)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "song_tag",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @JsonBackReference(value = "user-favoriteSongs")
    @Transient
    private Collection<UserDTO> users;

    @JsonSerialize(using = CustomUserJsonSerializer.class)
    @Transient
    private UserDTO uploader;

    @JsonBackReference(value = "playlist-song")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Playlist> playlists;

    @JsonBackReference("song-country")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @JsonBackReference("song-theme")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    private Duration duration;

    public Song(String title, Date releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", url='" + url + '\'' +
                '}';
    }
}
