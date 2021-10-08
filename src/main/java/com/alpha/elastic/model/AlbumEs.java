package com.alpha.elastic.model;

import com.alpha.model.entity.Album;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author thanhvt
 * @created 10/6/2021 - 7:12 PM
 * @project vengeance
 * @since 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Document(indexName = "album")
public class AlbumEs extends MediaEs {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "unaccentTitle")
    private String unaccentTitle;

    @JsonProperty(access = Access.READ_ONLY)
    private String coverUrl;

    @Field(type = FieldType.Object, name = "resourceMap")
    private ResourceMapEs resourceMap;

    @Field(type = FieldType.Nested, name = "artists", includeInParent = true)
    private List<ArtistEs> artists;

    public static AlbumEs fromAlbum(Album album) {
        return AlbumEs.builder()
            .id(album.getId())
            .title(album.getTitle())
            .unaccentTitle(album.getUnaccentTitle())
            .artists(
                album.getArtists().stream().map(ArtistEs::fromArtist).collect(Collectors.toList()))
            .build();
    }
}
