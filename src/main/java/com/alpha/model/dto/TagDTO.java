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
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true)
public class TagDTO {

    private Long id;

    @NotBlank
    private String name;

    @JsonBackReference(value = "song-tag")
    private Collection<SongDTO> songs;

    @JsonBackReference(value = "album-tag")
    private Collection<AlbumDTO> albums;
}
