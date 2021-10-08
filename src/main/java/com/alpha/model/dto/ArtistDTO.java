package com.alpha.model.dto;

import com.alpha.constant.EntityType;
import com.alpha.constant.EntityStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.Collection;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode()
@JsonIgnoreProperties(value = {"albums", "songs", "avatarResource", "avatarUrl"},
    allowGetters = true, ignoreUnknown = true)
public class ArtistDTO implements MediaDTO {

    private Long rn;

    private Long id;

    @NotBlank
    private String name;

    @JsonIgnore
    private String unaccentName;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date birthDate;

    private String avatarUrl;

    private ResourceInfoDTO avatarResource;

    private String biography;

    @Builder.Default
    private Long likeCount = 0L;

    private Boolean liked;

    @JsonProperty(access = Access.READ_ONLY)
    private UserInfoDTO uploader;

    private Date createTime;

    private Date updateTime;

    private EntityStatus status;

    private Integer sync;

    @JsonBackReference(value = "song-artist")
    private Collection<SongDTO> songs;

    @JsonBackReference(value = "album-artist")
    private Collection<AlbumDTO> albums;

    @Override
    @JsonIgnore
    public Collection<ArtistDTO> getArtists() {
        return null;
    }

    @Override
    @JsonIgnore
    public EntityType getType() {
        return EntityType.ARTIST;
    }
}
