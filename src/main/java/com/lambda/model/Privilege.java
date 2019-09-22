package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "privilege")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "privileges", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Role> roles;

    public Privilege() {
    }

    public Privilege(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }
}
