package com.alpha.model.dto;

import com.alpha.model.util.UploadObject;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = {"albums", "songs", "avatarBlobString", "avatarUrl"},
        allowGetters = true, ignoreUnknown = true)
public class ArtistDTO extends UploadObject {

    private Long id;

    @NotBlank
    private String name;

    @JsonIgnore
    private String unaccentName;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date birthDate;

    private String avatarUrl;

    private String avatarBlobString;

    private String biography;

    @JsonBackReference(value = "song-artist")
    private Collection<SongDTO> songs;

    @JsonBackReference(value = "album-artist")
    private Collection<AlbumDTO> albums;

    @Override
    public String getUrl() {
        return avatarUrl;
    }

    @Override
    public String createFileName(String ext) {
        return null;
    }

    @Override
    public String getFolder() {
        return null;
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
