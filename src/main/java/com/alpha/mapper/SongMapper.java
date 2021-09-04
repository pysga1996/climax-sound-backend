package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Song;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN,
    typeConversionPolicy = ReportingPolicy.ERROR,
    uses = {AlbumMapper.class, ArtistMapper.class, CommentMapper.class,
        CountryMapper.class, GenreMapper.class, ThemeMapper.class,
        TagMapper.class, PlaylistMapper.class, UserInfoMapper.class})
public abstract class SongMapper {

    @FullMapping
    @Mappings({
        @Mapping(target = "albums", qualifiedBy = PureMapping.class),
        @Mapping(target = "genres", qualifiedBy = PureMapping.class),
        @Mapping(target = "comments", qualifiedBy = PureMapping.class),
        @Mapping(target = "artists", qualifiedBy = PureMapping.class),
        @Mapping(target = "tags", qualifiedBy = PureMapping.class),
        @Mapping(target = "playlists", qualifiedBy = PureMapping.class),
        @Mapping(target = "country", qualifiedBy = PureMapping.class),
        @Mapping(target = "theme", qualifiedBy = PureMapping.class)
    })
    public abstract SongDTO entityToDto(Song song);

    @FullMapping
    @Mappings({
        @Mapping(target = "albums", qualifiedBy = PureMapping.class),
        @Mapping(target = "genres", qualifiedBy = PureMapping.class),
        @Mapping(target = "comments", qualifiedBy = PureMapping.class),
        @Mapping(target = "artists", qualifiedBy = PureMapping.class),
        @Mapping(target = "tags", qualifiedBy = PureMapping.class),
        @Mapping(target = "playlists", qualifiedBy = PureMapping.class),
        @Mapping(target = "country", qualifiedBy = PureMapping.class),
        @Mapping(target = "theme", qualifiedBy = PureMapping.class)
    })
    public abstract Song dtoToEntity(SongDTO song);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<SongDTO> entityToDtoList(List<Song> song);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Song> dtoToEntityList(List<SongDTO> songs);

    @Mappings({
        @Mapping(target = "albums", ignore = true),
        @Mapping(target = "genres", ignore = true),
        @Mapping(target = "comments", ignore = true),
        @Mapping(target = "artists", ignore = true),
        @Mapping(target = "tags", ignore = true),
        @Mapping(target = "playlists", ignore = true),
        @Mapping(target = "country", ignore = true),
        @Mapping(target = "theme", ignore = true)
    })
    @PureMapping
    public abstract SongDTO entityToDtoPure(Song song);

    @Mappings({
        @Mapping(target = "albums", ignore = true),
        @Mapping(target = "genres", ignore = true),
        @Mapping(target = "comments", ignore = true),
        @Mapping(target = "artists", ignore = true),
        @Mapping(target = "tags", ignore = true),
        @Mapping(target = "playlists", ignore = true),
        @Mapping(target = "country", ignore = true),
        @Mapping(target = "theme", ignore = true)
    })
    @PureMapping
    public abstract Song dtoToEntityPure(SongDTO song);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<SongDTO> entityToDtoListPure(List<Song> song);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Song> dtoToEntityListPure(List<SongDTO> songs);
}
