package com.alpha.model.dto;

import com.alpha.repositories.BaseRepository.HasArtists;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlbumDTO implements HasArtists {

    private Long rn;

    private Long id;

    @NotBlank
    private String title;

    private String unaccentTitle;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private Long listeningFrequency = 0L;

    private String coverUrl;

    private Duration duration;

    private ResourceInfoDTO coverResource;

    private Collection<GenreDTO> genres;

    private Collection<SongDTO> songs;

    private Collection<ArtistDTO> artists;

    private Collection<TagDTO> tags;

    private CountryDTO country;

    private UserInfoDTO uploader;

    private Collection<UserInfoDTO> users;
}
