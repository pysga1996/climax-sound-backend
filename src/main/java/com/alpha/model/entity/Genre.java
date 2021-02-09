package com.alpha.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "genre")
@Data
@NoArgsConstructor
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_id_gen")
    @SequenceGenerator(name = "genre_id_gen", sequenceName = "genre_id_seq", allocationSize = 1)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Song> songs;

    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    private Collection<Album> albums;

}


