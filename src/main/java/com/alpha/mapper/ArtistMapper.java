package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.entity.Artist;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class, AlbumMapper.class})
public abstract class ArtistMapper {

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class),
            @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract ArtistDTO entityToDto(Artist artist);

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class),
            @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract Artist dtoToEntity(ArtistDTO artistDTO);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<ArtistDTO> entityToDtoList(List<Artist> artist);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Artist> dtoToEntityList(List<ArtistDTO> artistDTO);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true),
            @Mapping(target = "albums", ignore = true)
    })
    public abstract ArtistDTO entityToDtoPure(Artist artist);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true),
            @Mapping(target = "albums", ignore = true)
    })
    public abstract Artist dtoToEntityPure(ArtistDTO artistDTO);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<ArtistDTO> entityToDtoListPure(List<Artist> artist);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Artist> dtoToEntityListPure(List<ArtistDTO> artistDTO);
}
