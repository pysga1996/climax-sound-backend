package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.entity.Comment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class,
        UserInfoMapper.class})
public abstract class CommentMapper {

    @FullMapping
    @Mappings({
            @Mapping(target = "song", qualifiedBy = PureMapping.class)
    })
    public abstract CommentDTO entityToDto(Comment comment);

    @FullMapping
    @Mappings({
            @Mapping(target = "song", qualifiedBy = PureMapping.class)
    })
    public abstract Comment dtoToEntity(CommentDTO comment);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<CommentDTO> entityToDtoList(List<Comment> comments);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Comment> dtoToEntityList(List<CommentDTO> comments);

    @PureMapping
    @Mappings({
            @Mapping(target = "song", ignore = true)
    })
    public abstract CommentDTO entityToDtoPure(Comment comment);

    @PureMapping
    @Mappings({
            @Mapping(target = "song", ignore = true)
    })
    public abstract Comment dtoToEntityPure(CommentDTO comment);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<CommentDTO> entityToDtoListPure(List<Comment> comments);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Comment> dtoToEntityListPure(List<CommentDTO> comments);
}
