package com.alpha.model.entity;

import com.alpha.constant.Folder;
import com.alpha.constant.MediaRef;
import com.alpha.constant.MediaType;
import com.alpha.constant.EntityStatus;
import com.alpha.util.formatter.StringAccentRemover;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "artist")
public class Artist extends Media {

    @Transient
    private Long rn;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id_gen")
    @SequenceGenerator(name = "artist_id_gen", sequenceName = "artist_id_seq", allocationSize = 1)
    @ToString.Include
    private Long id;

    @ToString.Include
    private String name;

    private String unaccentName;

    @ToString.Include
    private Date birthDate;

    @Transient
    private String avatarUrl;

    @Transient
    private ResourceInfo avatarResource;

    //    @Column(columnDefinition = "LONGTEXT")
    @Column(columnDefinition = "TEXT")
    private String biography;

    @Transient
    private Boolean liked;

    @Builder.Default
    @Column(name = "like_count")
    @ColumnDefault("0")
    private Long likeCount = 0L;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserInfo uploader;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "status")
    @Convert(converter = EntityStatus.StatusAttributeConverter.class)
    private EntityStatus status;

    @Column(name = "sync")
    private Integer sync;

    //    @JsonBackReference(value = "song-artist")
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @Exclude
    private Collection<Song> songs;

    //    @JsonBackReference(value = "album-artist")
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @Exclude
    private Collection<Album> albums;

    @Override
    public ResourceInfo generateResource(MultipartFile file) {
        if (id == null) {
            throw new RuntimeException("Media host id is null!!");
        }
        String ext = this.getExtension(file);
        String fileName = MediaRef.ARTIST_AVATAR.name()
            + " - "
            + id
            + " - "
            + StringAccentRemover.removeStringAccent(this.name)
            + "."
            + ext;
        fileName = this.normalizeFileName(fileName);
        return ResourceInfo.builder()
            .mediaId(id)
            .extension(ext)
            .folder(Folder.AVATAR)
            .fileName(fileName)
            .status(EntityStatus.INACTIVE)
            .mediaType(MediaType.IMAGE)
            .mediaRef(MediaRef.ARTIST_AVATAR)
            .build();
    }
}
