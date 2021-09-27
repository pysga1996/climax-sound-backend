package com.alpha.model.entity;

import com.alpha.constant.Folder;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.Status;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "song")
public class Song extends Media {

    @Transient
    private Long rn;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_id_gen")
    @SequenceGenerator(name = "song_id_gen", sequenceName = "song_id_seq", allocationSize = 1)
    @ToString.Include
    private Long id;

    @ToString.Include
    private String title;

    private String unaccentTitle;

    @ToString.Include
    private Date releaseDate;

    @Transient
    @ToString.Include
    private String url;

    @Transient
    private ResourceInfo audioResource;

    @Transient
    private Collection<Comment> comments;

    @Builder.Default
    @ColumnDefault("0")
    private Long displayRating = 0L;

    @Builder.Default
    @Column(name = "listening_frequency")
    @ColumnDefault("0")
    private Long listeningFrequency = 0L;

    @Transient
    private Boolean liked;

    @Builder.Default
    @Column(name = "like_count")
    @ColumnDefault("0")
    private Long likeCount = 0L;

    //    @Column(columnDefinition = "LONGTEXT")
    @Column(columnDefinition = "TEXT")
    private String lyric;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "song_artist",
        joinColumns = @JoinColumn(
            name = "song_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "artist_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "artist_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Artist> artists;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "song_tag",
        joinColumns = @JoinColumn(
            name = "song_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "tag_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "tag_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Tag> tags;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "song_genre",
        joinColumns = @JoinColumn(
            name = "song_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "genre_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "genre_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserInfo uploader;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "songs")
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Playlist> playlists;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Theme theme;

    //    @Convert(converter = DurationConverter.class)
    private Duration duration;

    @Override
    public ResourceInfo generateResource(MultipartFile file) {
        if (id == null) {
            throw new RuntimeException("Media host id is null!!");
        }
        String ext = this.getExtension(file);
        String fileName = MediaRef.SONG_AUDIO.name() + " - " + id + " - " + this
            .createFilenameFromArtists(id, title, artists, ext);
        fileName = this.normalizeFileName(fileName);
        return ResourceInfo.builder()
            .mediaId(id)
            .extension(ext)
            .folder(Folder.AUDIO)
            .fileName(fileName)
            .status(Status.INACTIVE)
            .mediaType(MediaType.AUDIO)
            .mediaRef(MediaRef.SONG_AUDIO)
            .build();
    }
}
