package com.alpha.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"songs", "albums"}, allowGetters = true, ignoreUnknown = true)
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @JsonManagedReference("song-theme")
    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> songs;

    @JsonManagedReference("album-theme")
    @OneToMany(mappedBy = "theme", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> albums;

    public Theme(String name) {
        this.name = name;
    }
}
