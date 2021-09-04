package com.alpha.model.entity;

import com.alpha.constant.Folder;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.Status;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "album")
public class Album extends Media {

    @Transient
    private Long rn;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_id_gen")
    @SequenceGenerator(name = "album_id_gen", sequenceName = "album_id_seq", allocationSize = 1)
    @ToString.Include
    private Long id;

    @NotBlank
    @ToString.Include
    private String title;

    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ToString.Include
    private Date releaseDate;

    @ColumnDefault("0")
    private Long listeningFrequency = 0L;

    @Transient
    private String coverUrl;

    @Transient
    private ResourceInfo coverResource;

    private Duration duration;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "album_genre",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "genre_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "genre_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "album_song",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "song_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "song_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Song> songs;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "album_artist",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "artist_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "artist_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Artist> artists;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "album_tag",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "tag_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "tag_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    private Collection<Tag> tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserInfo uploader;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_albums",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "username", referencedColumnName = "username"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "username"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<UserInfo> users;

    @Override
    public ResourceInfo generateResource(MultipartFile file) {
        if (id == null) {
            throw new RuntimeException("Media host id is null!!");
        }
        String ext = this.getExtension(file);
        String fileName = MediaRef.ALBUM_COVER.name() + " - " + id + " - " + this.createFilenameFromArtists(id, title, artists, ext);
        fileName = this.normalizeFileName(fileName);
        return ResourceInfo.builder()
            .mediaId(id)
            .extension(ext)
            .folder(Folder.COVER)
            .fileName(fileName)
            .status(Status.INACTIVE)
            .mediaType(MediaType.IMAGE)
            .mediaRef(MediaRef.ALBUM_COVER)
            .build();
    }
}
