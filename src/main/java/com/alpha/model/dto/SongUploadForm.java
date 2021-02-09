package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SongUploadForm {
    private Long id;

    @NotBlank
    private String title;

    @JsonIgnore
    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String url;

    @JsonIgnore
    private Collection<CommentDTO> comments;

    @Builder.Default
    private Long displayRating = 0L;

    @Builder.Default
    private Long listeningFrequency = 0L;

    private Boolean liked;

    private String lyric;

    @JsonIgnore
    private String blobString;

    private Collection<ArtistDTO> artists;

    private Collection<AlbumDTO> albums;

    private String tags;

    private Collection<GenreDTO> genres;

    private Collection<UserDTO> users;

    private UserDTO uploader;

    private Collection<PlaylistDTO> playlists;

    private CountryDTO country;

    private ThemeDTO theme;

    private Duration duration;
}
