package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true, ignoreUnknown = true)
public class ThemeDTO {

    private Integer id;

    @NotBlank
    private String name;

    @JsonManagedReference("theme-song")
    @JsonBackReference("song-theme")
    private Collection<SongDTO> songs;

//    @JsonManagedReference("album-theme")
//    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
//    @Fetch(value = FetchMode.SUBSELECT)
//    private Collection<Album> albums;

}
