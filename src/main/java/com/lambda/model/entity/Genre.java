package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Data
@RequiredArgsConstructor
public class Genre {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    private final String name;

    @JsonBackReference("song-genre")
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Song> songs;

    @JsonBackReference("album-genre")
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Album> albums;
}


