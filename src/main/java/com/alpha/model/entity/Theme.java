package com.alpha.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "theme")
@Data
@NoArgsConstructor
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "theme_id_gen")
    @SequenceGenerator(name = "theme_id_gen", sequenceName = "theme_id_seq", allocationSize = 1)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

//    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
//    @Fetch(value = FetchMode.SUBSELECT)
//    private Collection<Album> albums;

}
