package com.alpha.mapper;

import com.alpha.mapper.annotation.FullMapping;
import com.alpha.mapper.annotation.PureMapping;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.entity.Comment;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR, uses = {SongMapper.class,
    UserInfoMapper.class})
public abstract class CommentMapper {

    @FullMapping
    public abstract CommentDTO entityToDto(Comment comment);

    @FullMapping
    public abstract Comment dtoToEntity(CommentDTO comment);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<CommentDTO> entityToDtoList(List<Comment> comments);

    @FullMapping
    @IterableMapping(qualifiedBy = FullMapping.class)
    public abstract List<Comment> dtoToEntityList(List<CommentDTO> comments);

    @PureMapping
    public abstract CommentDTO entityToDtoPure(Comment comment);

    @PureMapping
    public abstract Comment dtoToEntityPure(CommentDTO comment);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<CommentDTO> entityToDtoListPure(List<Comment> comments);

    @PureMapping
    @IterableMapping(qualifiedBy = PureMapping.class)
    public abstract List<Comment> dtoToEntityListPure(List<CommentDTO> comments);
}
