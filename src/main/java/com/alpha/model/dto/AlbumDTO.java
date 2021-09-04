package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlbumDTO extends UploadDTO {

    private Long id;

    @NotBlank
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private Long listeningFrequency = 0L;

    private String coverUrl;

    private Duration duration;

    private String coverBlobString;

    private Collection<GenreDTO> genres;

    @JsonBackReference("song-album")
    private Collection<SongDTO> songs;

    private Collection<ArtistDTO> artists;

    private Collection<TagDTO> tags;

    private CountryDTO country;

    private UserInfoDTO uploader;

    private Collection<UserInfoDTO> users;

    @Override
    public String getUrl() {
        return coverUrl;
    }

    @Override
    public String createFileName(String ext) {
        artists = this.getArtists();
        String artistsString = this.getArtistDTOString(artists);
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
