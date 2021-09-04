package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Collection;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
