package com.alpha.model.entity;

import com.alpha.model.util.UploadObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "artist")
public class Artist extends UploadObject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_id_gen")
    @SequenceGenerator(name = "artist_id_gen", sequenceName = "artist_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String unaccentName;

    private Date birthDate;

    private String avatarUrl;

    private String avatarBlobString;

    //    @Column(columnDefinition = "LONGTEXT")
    @Column(columnDefinition = "TEXT")
    private String biography;

    //    @JsonBackReference(value = "song-artist")
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    //    @JsonBackReference(value = "album-artist")
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public String getUrl() {
        return avatarUrl;
    }

    @Override
    public String createFileName(String ext) {
        return this.getId().toString().concat(" - ").concat(this.getName()).concat(".").concat(ext);
    }

    @Override
    public String getFolder() {
        return "avatar";
    }

    @Override
    public String getBlobString() {
        return avatarBlobString;
    }

    @Override
    public void setBlobString(String blobString) {
        this.setAvatarBlobString(blobString);
    }
}
