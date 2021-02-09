package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

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
