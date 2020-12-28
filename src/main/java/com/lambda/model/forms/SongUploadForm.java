package com.lambda.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lambda.model.entities.*;
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
    private Collection<Comment> comments;

    @Builder.Default
    private Long displayRating = 0L;

    @Builder.Default
    private Long listeningFrequency = 0L;

    private Boolean liked;

    private String lyric;

    @JsonIgnore
    private String blobString;

    private Collection<Artist> artists;

    private Collection<Album> albums;

    private String tags;

    private Collection<Genre> genres;

    private Collection<User> users;

    private User uploader;

    private Collection<Playlist> playlists;

    private Country country;

    private Theme theme;

    private Duration duration;
}
