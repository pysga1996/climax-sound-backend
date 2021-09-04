package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.entity.Country;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class, AlbumMapper.class})
public abstract class CountryMapper {

    @FullMapping
    @Mappings({
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract CountryDTO entityToDto(Country country);

    @FullMapping
    @Mappings({
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract Country dtoToEntity(CountryDTO country);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<CountryDTO> entityToDtoList(List<Country> countries);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Country> dtoToEntityList(List<CountryDTO> countries);

    @Mappings({
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "albums", ignore = true)
    })
    @PureMapping
    public abstract CountryDTO entityToDtoPure(Country country);

    @Mappings({
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "albums", ignore = true)
    })
    @PureMapping
    public abstract Country dtoToEntityPure(CountryDTO country);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<CountryDTO> entityToDtoListPure(List<Country> countries);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Country> dtoToEntityListPure(List<CountryDTO> countries);

}
