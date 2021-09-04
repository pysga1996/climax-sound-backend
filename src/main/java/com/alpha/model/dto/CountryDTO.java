package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"})
public class CountryDTO {

    private Integer id;

    @NotBlank
    private String name;

    @JsonBackReference("song-country")
    private Collection<SongDTO> songs;

    private Collection<AlbumDTO> albums;
}
