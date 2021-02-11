package com.alpha.model.entity;

import com.alpha.model.dto.UserDTO;
import com.alpha.model.util.UploadObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "album")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Album extends UploadObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_id_gen")
    @SequenceGenerator(name = "album_id_gen", sequenceName = "album_id_seq", allocationSize = 1)
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

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private UserInfo uploader;

    @Transient
    private Collection<UserDTO> users;

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    @Override
    public String getUrl() {
        return coverUrl;
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
        return "cover";
    }

    @Override
    public String getBlobString() {
        return coverBlobString;
    }

    @Override
    public void setBlobString(String blobString) {
        this.setCoverBlobString(blobString);
    }
}
