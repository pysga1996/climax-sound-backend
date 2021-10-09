package com.alpha.model.dto;

import com.alpha.constant.ModelStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
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

    @JsonBackReference("album-country")
    private Collection<AlbumDTO> albums;

    private Date createTime;

    private Date updateTime;

    private ModelStatus status;
}
