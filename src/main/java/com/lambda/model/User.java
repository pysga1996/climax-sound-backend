package com.lambda.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Collection;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})")
    private String password;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @JsonManagedReference
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> favoriteSongs;

    @JsonManagedReference
    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> favoriteAlbums;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    public User() {
    }

    public User(String username, String password, Collection<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Collection<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(Collection<Song> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    public Collection<Album> getFavoriteAlbums() {
        return favoriteAlbums;
    }

    public void setFavoriteAlbums(Collection<Album> favoriteAlbums) {
        this.favoriteAlbums = favoriteAlbums;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
