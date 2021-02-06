package com.alpha.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;

@Data
@EqualsAndHashCode()
@NoArgsConstructor
@JsonIgnoreProperties(value = {"avatarBlobString"
        , "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired"}, allowGetters = true, ignoreUnknown = true)
public class UserDTO implements UserDetails {

    Collection<Playlist> playlists;
    Collection<Song> uploadedSong;
    private Long id;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+([a-zA-Z0-9]([_\\- ])[a-zA-Z0-9])*[a-zA-Z0-9]+${8,}")
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
    private String avatarBlobString;
    private Collection<RoleDTO> authorities;
    @JsonIgnore
    private Collection<Song> favoriteSongs;
    @JsonIgnore
    private Collection<Album> favoriteAlbums;
    @JsonIgnore
    private Collection<Comment> comments;

    private SettingDTO setting;

    private boolean enabled = false;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    public UserDTO(String username, String password, Collection<RoleDTO> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.enabled = false;
    }

    public UserDTO(String firstName, String lastName, Boolean gender, String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.enabled = false;
    }

    public UserDTO(String username, String password, String firstName, String lastName, Boolean gender, Date birthDate, String phoneNumber, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.enabled = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender=" + gender +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
