package com.alpha.elastic.model;

import com.alpha.model.entity.Artist;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
@Document(indexName = "artist")
public class ArtistEs extends MediaEs {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Text, name = "unaccentTitle")
    private String unaccentName;

    @JsonProperty(access = Access.READ_ONLY)
    private String avatarUrl;

    @Field(type = FieldType.Object, name = "resourceMap")
    private ResourceMapEs resourceMap;

    public static ArtistEs fromArtist(Artist artist) {
        return ArtistEs.builder()
            .id(artist.getId())
            .name(artist.getName())
            .unaccentName(artist.getUnaccentName())
            .build();
    }
}
