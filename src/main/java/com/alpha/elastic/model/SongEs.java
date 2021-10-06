package com.alpha.elastic.model;

import com.alpha.model.entity.Song;
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
@Document(indexName = "song")
public class SongEs extends MediaEs {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "unaccentTitle")
    private String unaccentTitle;

    @Field(type = FieldType.Object, name = "resourceMap")
    private ResourceMapEs resourceMap;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<ArtistEs> artists;

    public static SongEs fromSong(Song song) {
        return SongEs.builder()
            .id(song.getId())
            .title(song.getTitle())
            .unaccentTitle(song.getUnaccentTitle())
            .artists(
                song.getArtists().stream().map(ArtistEs::fromArtist).collect(Collectors.toList()))
            .build();
    }
}
