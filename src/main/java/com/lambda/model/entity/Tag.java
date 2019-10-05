package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private final String name;

    @JsonBackReference(value = "song-tag")
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonBackReference(value = "album-tag")
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;
}
