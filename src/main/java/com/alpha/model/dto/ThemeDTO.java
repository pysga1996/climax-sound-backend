package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Collection;
import java.util.Date;
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

    @JsonBackReference("song-theme")
    private Collection<SongDTO> songs;

    private Date createTime;

    private Date updateTime;

    private Integer status;

}
