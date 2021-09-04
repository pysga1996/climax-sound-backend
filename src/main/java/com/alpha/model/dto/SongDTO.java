package com.alpha.model.dto;

import com.alpha.repositories.BaseRepository.HasArtists;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
@JsonIgnoreProperties(value = {"comments", "liked", "albums", "genres", "users",
    "playlists", "theme", "uploader"}, allowGetters = true, ignoreUnknown = true)
public class SongDTO implements HasArtists {

    private Long rn;

    private Long id;

    @NotBlank
    private String title;

    @JsonIgnore
    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String url;

    private ResourceInfoDTO audioResource;

    @JsonManagedReference(value = "song-comment")
    private Collection<CommentDTO> comments;

    private Long displayRating = 0L;

    private Long listeningFrequency = 0L;

    private Boolean liked;

    private String lyric;

    @JsonManagedReference(value = "song-artist")
    private Collection<ArtistDTO> artists;

    @JsonManagedReference("song-album")
    private Collection<AlbumDTO> albums;

    @JsonManagedReference("song-tag")
    private Collection<TagDTO> tags;

    @JsonManagedReference("song-genre")
    private Collection<GenreDTO> genres;

    @JsonBackReference(value = "user-favoriteSongs")
    private Collection<UserInfoDTO> users;

    private UserInfoDTO uploader;

    @JsonBackReference(value = "playlist-song")
    private Collection<PlaylistDTO> playlists;

    @JsonManagedReference("song-country")
//    @JsonBackReference("song-country")
    private CountryDTO country;

    @JsonManagedReference("song-theme")
    @JsonBackReference("theme-song")
    private ThemeDTO theme;

    private Duration duration;

    private Long likeCount;
}
