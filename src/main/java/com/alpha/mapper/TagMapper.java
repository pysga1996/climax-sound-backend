package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Tag;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class,
    AlbumMapper.class})
public abstract class TagMapper {

    @FullMapping
    @Mappings({
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract TagDTO entityToDto(Tag tag);

    @FullMapping
    @Mappings({
        @Mapping(target = "songs", qualifiedBy = PureMapping.class),
        @Mapping(target = "albums", qualifiedBy = PureMapping.class)
    })
    public abstract Tag dtoToEntity(TagDTO tag);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<TagDTO> entityToDtoList(List<Tag> tags);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Tag> dtoToEntityList(List<TagDTO> tags);

    @PureMapping
    @Mappings({
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "albums", ignore = true)
    })
    public abstract TagDTO entityToDtoPure(Tag tag);

    @PureMapping
    @Mappings({
        @Mapping(target = "songs", ignore = true),
        @Mapping(target = "albums", ignore = true)
    })
    public abstract Tag dtoToEntityPure(TagDTO tag);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<TagDTO> entityToDtoListPure(List<Tag> tags);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Tag> dtoToEntityListPure(List<TagDTO> tags);
}
