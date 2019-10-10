package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.*;
import com.lambda.model.util.MediaObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@JsonIgnoreProperties(value = {"comments", "albums", "tags", "genres", "users", "playlists", "country", "theme", "uploader", "blobId"}, allowGetters = true, ignoreUnknown=true)
//@JsonIdentityInfo(
//        generator = ObjectIdGenerators.PropertyGenerator.class,
//        property = "id")
public class Song extends MediaObject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String url;

    @JsonManagedReference(value = "song-comment")
    @OneToMany(mappedBy = "song")
    private Collection<Comment> comments;

    private Double displayRating;

    private String lyric;

    private String blobId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "artist_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @JsonBackReference(value = "album-song")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    @JsonManagedReference(value = "song-tag")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_tag",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @JsonManagedReference(value = "song-genre")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "song_genre",
            joinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "genre_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @JsonBackReference(value = "user-favoriteSongs")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "favoriteSongs")
    private Collection<User> users;

    @JsonBackReference(value = "user-uploadedSong")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User uploader;

    @JsonBackReference(value = "playlist-song")
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Playlist> playlists;

    @JsonBackReference(value = "song-country")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    @JsonBackReference(value = "song-theme")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    public Song(String name, Date releaseDate) {
        this.name = name;
        this.releaseDate = releaseDate;
    }
}
