package com.alpha.model.dto;

import com.alpha.constant.EntityType;
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
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDTO implements MediaDTO {

    private Long rn;

    private Long id;

    @NotBlank
    private String title;

    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private Long listeningFrequency = 0L;

    private Long likeCount = 0L;

    private Boolean liked;

    private String coverUrl;

    private Duration duration;

    private String description;

    private ResourceInfoDTO coverResource;

    private Collection<GenreDTO> genres;

    private Collection<SongDTO> songs;

    private Collection<ArtistDTO> artists;

    private Collection<TagDTO> tags;

    private CountryDTO country;

    private ThemeDTO theme;

    private UserInfoDTO uploader;

    @JsonProperty(access = Access.WRITE_ONLY)
    private AlbumAdditionalInfoDTO additionalInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumAdditionalInfoDTO {

        private String description;

        private Collection<TagDTO> tags;

        private Collection<GenreDTO> genres;

        private CountryDTO country;

        private ThemeDTO theme;
    }

    @Override
    public EntityType getType() {
        return EntityType.ALBUM;
    }
}
