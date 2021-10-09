package com.alpha.elastic.model;

import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

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
@Setting(settingPath = "/elastic_setting/lowercase_normalizer.json")
public class SongEs extends MediaEs {

    @Id
    private Long id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "unaccentTitle")
    private String unaccentTitle;

    @Field(type = FieldType.Long, name = "listeningFrequency")
    private Long listeningFrequency;

    @Field(type = FieldType.Long, name = "duration")
    private Long duration;

    @Field(type = FieldType.Keyword, name = "tags", normalizer = "lowercase_normalizer")
    private String[] tags;

    @Field(type = FieldType.Object, name = "resourceMap")
    private ResourceMapEs resourceMap;

    @Field(type = FieldType.Nested, name = "artists", includeInParent = true)
    private List<ArtistEs> artists;

    public static SongEs fromSong(Song song) {
        return SongEs.builder()
            .id(song.getId())
            .title(song.getTitle())
            .unaccentTitle(song.getUnaccentTitle())
            .listeningFrequency(song.getListeningFrequency())
            .duration(song.getDuration().getSeconds())
            .artists(
                song.getArtists().stream().map(ArtistEs::fromArtist).collect(Collectors.toList()))
            .tags(song.getTags().parallelStream().map(Tag::getName).collect(Collectors.toList())
                .toArray(new String[]{}))
            .build();
    }
}
