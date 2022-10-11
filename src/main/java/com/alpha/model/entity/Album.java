package com.alpha.model.entity;

import com.alpha.constant.Folder;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.ModelStatus;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
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
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "album")
@Where(clause = "status = 1")
@NamedEntityGraph(
    name = "album-artist",
    attributeNodes = {
        @NamedAttributeNode("artists")
    }
)
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

    @Builder.Default
    @Column(name = "listening_frequency")
    @ColumnDefault("0")
    private Long listeningFrequency = 0L;

    @Builder.Default
    @Column(name = "like_count")
    @ColumnDefault("0")
    private Long likeCount = 0L;

    @Transient
    private String coverUrl;

    @Transient
    private ResourceInfo coverResource;

    private Duration duration;

    @Column(name = "description")
    private String description;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "status")
    @Convert(converter = ModelStatus.StatusAttributeConverter.class)
    private ModelStatus status;

    @Column(name = "sync")
    private Integer sync;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "album_genre",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "genre_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "genre_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Genre> genres;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "album_song",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "song_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"song_id", "song_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "album_artist",
        joinColumns = @JoinColumn(
            name = "album_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(
            name = "artist_id", referencedColumnName = "id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "artist_id"}))
    @Fetch(value = FetchMode.SUBSELECT)
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
    private Collection<Tag> tags;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theme_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Theme theme;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username", referencedColumnName = "username")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserInfo uploader;

    @Override
    public ResourceInfo generateResource(MultipartFile file) {
        if (id == null) {
            throw new IllegalArgumentException("Media host id is null!!");
        }
        String ext = this.getExtension(file);
        String fileName = MediaRef.ALBUM_COVER.name() + " - " + id + " - " + this
            .createFilenameFromArtists(id, title, artists, ext);
        fileName = this.normalizeFileName(fileName);
        return ResourceInfo.builder()
            .mediaId(id)
            .extension(ext)
            .folder(Folder.COVER)
            .fileName(fileName)
            .status(ModelStatus.INACTIVE)
            .mediaType(MediaType.IMAGE)
            .mediaRef(MediaRef.ALBUM_COVER)
            .build();
    }
}
