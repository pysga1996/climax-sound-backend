package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true)
public class Artist {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private final String name;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private final Date birthDate;

    private final String avatarUrl;

    private final String biography;

    @JsonBackReference
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonBackReference
    @ManyToMany(mappedBy = "artists", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;
}
