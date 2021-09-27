package com.alpha.model.dto;

import com.alpha.constant.EntityType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"comments", "albums", "users",
    "playlists", "uploader"}, allowGetters = true, ignoreUnknown = true)
public class SongDTO implements MediaDTO {

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

    @Builder.Default
    private Long displayRating = 0L;

    @Builder.Default
    private Long listeningFrequency = 0L;

    @Builder.Default
    private Long likeCount = 0L;

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

    private UserInfoDTO uploader;

    @JsonBackReference(value = "playlist-song")
    private Collection<PlaylistDTO> playlists;

    @JsonManagedReference("song-country")
    private CountryDTO country;

    @JsonManagedReference("song-theme")
    private ThemeDTO theme;

    private Duration duration;

    @JsonProperty(access = Access.WRITE_ONLY)
    private SongAdditionalInfoDTO additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SongAdditionalInfoDTO {

        private String lyric;

        private Collection<TagDTO> tags;

        private Collection<GenreDTO> genres;

        private CountryDTO country;

        private ThemeDTO theme;
    }


    @Override
    @JsonIgnore
    public EntityType getType() {
        return EntityType.SONG;
    }
}
