package com.alpha.model.dto;

import com.alpha.model.util.UploadObject;
import com.alpha.util.helper.CustomUserJsonSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = {"comments", "liked", "albums", "genres", "users",
        "playlists", "theme", "uploader"}, allowGetters = true, ignoreUnknown = true)
public class SongDTO extends UploadObject {

    private Long id;

    @NotBlank
    private String title;

    @JsonIgnore
    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String url;

    @JsonManagedReference(value = "song-comment")
    private Collection<CommentDTO> comments;

    private Long displayRating = 0L;

    private Long listeningFrequency = 0L;

    private Boolean liked;

    private String lyric;

    private String blobString;

    @JsonManagedReference(value = "song-artist")
    private Collection<ArtistDTO> artists;

    @JsonManagedReference("song-album")
    private Collection<AlbumDTO> albums;

    @JsonManagedReference("song-tag")
    private Collection<TagDTO> tags;

    @JsonManagedReference("song-genre")
    private Collection<GenreDTO> genres;

    @JsonBackReference(value = "user-favoriteSongs")
    private Collection<UserDTO> users;

    @JsonSerialize(using = CustomUserJsonSerializer.class)
    private UserDTO uploader;

    @JsonBackReference(value = "playlist-song")
    private Collection<PlaylistDTO> playlists;

    @JsonManagedReference("song-country")
//    @JsonBackReference("song-country")
    private CountryDTO country;

    @JsonManagedReference("song-theme")
    @JsonBackReference("theme-song")
    private ThemeDTO theme;

    private Duration duration;

    @Override
    public String createFileName(String ext) {
        artists = this.getArtists();
        String artistsString = this.getArtistDTOString(artists);
        return StringUtils.cleanPath(this.getId().toString().concat(" - ")
                .concat(this.getTitle()).concat(artistsString).concat(".").concat(ext));
    }

    @Override
    public String getFolder() {
        return "audio";
    }
}
