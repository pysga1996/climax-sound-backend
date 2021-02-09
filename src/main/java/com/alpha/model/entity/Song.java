package com.alpha.model.entity;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.util.UploadObject;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "song")
public class Song extends UploadObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_gen")
    @SequenceGenerator(name = "song_id_gen", sequenceName = "song_id_seq", allocationSize = 1)
    private Long id;

    private String title;

    private String unaccentTitle;

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

    @Transient
    private Boolean liked;

    //    @Column(columnDefinition = "LONGTEXT")
    @Column(columnDefinition = "TEXT")
    private String lyric;

    private String blobString;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(schema = "alpha_sound",
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

    @Transient
    private Collection<UserDTO> users;

    @Transient
    private UserDTO uploader;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Playlist> playlists;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    private Duration duration;

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public String createFileName(String ext) {
        artists = this.getArtists();
        String artistsString = this.getArtistString(artists);
        return StringUtils.cleanPath(this.getId().toString().concat(" - ")
                .concat(this.getTitle()).concat(artistsString).concat(".").concat(ext));
    }

    @Override
    public String getFolder() {
        return "audio";
    }
}
