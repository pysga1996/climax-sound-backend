package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"user", "songs"}, allowGetters = true)
public class PlaylistDTO {

    private Long id;

    @NotBlank
    private String title;

    private Long userId;

    private UserDTO user;

    private Collection<SongDTO> songs;
}
