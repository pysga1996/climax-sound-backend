package com.lambda.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"roles", "favoriteSongs", "favoriteAlbums", "comments", "playlists", "avatarBlobId"}, allowGetters = true, ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+([a-zA-Z0-9]([_\\- ])[a-zA-Z0-9])*[a-zA-Z0-9]+${8,}")
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(255) COLLATE utf8mb4_bin")
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull
    private Boolean gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Pattern(regexp = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- ()]*${10,13}")
    private String phoneNumber;

    @Email
    private String email;

    private String avatarUrl;

    private String avatarBlobId;

    @JsonManagedReference("user-role")
    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @Fetch(value = FetchMode.SUBSELECT)
    Collection<Playlist> playlists;

    @JsonManagedReference("user-favoriteSongs")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favoriteSongs",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "song_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Song> favoriteSongs;

    @JsonManagedReference("user-favoriteAlbums")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favoriteAlbums",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "album_id", referencedColumnName = "id"))
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Album> favoriteAlbums;

    @JsonManagedReference("user-uploadedSong")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    Collection<Song> uploadedSong;

    @JsonManagedReference(value = "user-comment")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Collection<Comment> comments;

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    public User(String username, String password, Collection<Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password, String firstName, String lastName, Boolean gender, Date birthDate, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
