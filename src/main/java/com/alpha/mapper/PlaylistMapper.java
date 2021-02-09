package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.PlaylistDTO;
import com.alpha.model.entity.Playlist;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class})
public abstract class PlaylistMapper {

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class)
    })
    public abstract PlaylistDTO entityToDto(Playlist playlist);

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class)
    })
    public abstract Playlist dtoToEntity(PlaylistDTO playlist);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<PlaylistDTO> entityToDtoList(List<Playlist> playlist);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Playlist> dtoToEntityList(List<PlaylistDTO> playlist);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true)
    })
    public abstract PlaylistDTO entityToDtoPure(Playlist playlist);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true)
    })
    public abstract Playlist dtoToEntityPure(PlaylistDTO playlist);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<PlaylistDTO> entityToDtoListPure(List<Playlist> playlist);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Playlist> dtoToEntityListPure(List<PlaylistDTO> playlist);
}
