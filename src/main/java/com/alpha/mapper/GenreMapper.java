package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.entity.Genre;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class, AlbumMapper.class})
public abstract class GenreMapper {

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class),
            @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract GenreDTO entityToDto(Genre genre);

    @FullMapping
    @Mappings({
            @Mapping(target = "songs", qualifiedBy = PureMapping.class),
            @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract Genre dtoToEntity(GenreDTO genre);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<GenreDTO> entityToDtoList(List<Genre> genres);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Genre> dtoToEntityList(List<GenreDTO> genres);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true),
            @Mapping(target = "albums", ignore = true)
    })
    public abstract GenreDTO entityToDtoPure(Genre genre);

    @PureMapping
    @Mappings({
            @Mapping(target = "songs", ignore = true),
            @Mapping(target = "albums", ignore = true)
    })
    public abstract Genre dtoToEntityPure(GenreDTO genre);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<GenreDTO> entityToDtoListPure(List<Genre> genres);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Genre> dtoToEntityListPure(List<GenreDTO> genres);

}
