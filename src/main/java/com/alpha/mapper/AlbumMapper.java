package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.AlbumDTO.AlbumAdditionalInfoDTO;
import com.alpha.model.entity.Album;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN,
    typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class,
    GenreMapper.class, ArtistMapper.class, TagMapper.class, UserInfoMapper.class})
public abstract class AlbumMapper {

    @FullMapping
    @Mappings({
        @Mapping(target = "genres", qualifiedBy = PureMapping.class),
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "artists", qualifiedBy = PureMapping.class),
        @Mapping(target = "tags", qualifiedBy = PureMapping.class)
    })
    public abstract AlbumDTO entityToDto(Album album);

    @FullMapping
    @Mappings({
        @Mapping(target = "genres", qualifiedBy = PureMapping.class),
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "artists", qualifiedBy = PureMapping.class),
        @Mapping(target = "tags", qualifiedBy = PureMapping.class)
    })
    public abstract Album dtoToEntity(AlbumDTO albums);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<AlbumDTO> entityToDtoList(List<Album> album);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Album> dtoToEntityList(List<AlbumDTO> albums);

    @PureMapping
    @Mappings({
        @Mapping(target = "genres", ignore = true),
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "artists", ignore = true),
        @Mapping(target = "tags", ignore = true),
        @Mapping(target = "country", ignore = true),
        @Mapping(target = "theme", ignore = true)
    })
    public abstract AlbumDTO entityToDtoPure(Album album);

    @PureMapping
    @Mappings({
        @Mapping(target = "genres", ignore = true),
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "artists", ignore = true),
        @Mapping(target = "tags", ignore = true),
        @Mapping(target = "country", ignore = true),
        @Mapping(target = "theme", ignore = true)
    })
    public abstract Album dtoToEntityPure(AlbumDTO album);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<AlbumDTO> entityToDtoListPure(List<Album> albums);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Album> dtoToEntityListPure(List<AlbumDTO> albums);

    @Mappings({
        @Mapping(source = "description", target = "description"),
        @Mapping(source = "genres", target = "genres"),
        @Mapping(source = "tags", target = "tags"),
        @Mapping(source = "country", target = "country"),
        @Mapping(source = "theme", target = "theme")
    })
    @PureMapping
    public abstract Album dtoToEntityAdditional(AlbumAdditionalInfoDTO songAdditionalInfoDTO);
}
