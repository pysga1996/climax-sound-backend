package com.alpha.mapper;

import com.alpha.model.dto.CommentDTO;
import com.alpha.model.entity.Comment;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T09:33:53+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl extends CommentMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public CommentDTO entityToDto(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId( comment.getId() );
        commentDTO.setContent( comment.getContent() );
        commentDTO.setLocalDateTime( comment.getLocalDateTime() );
        commentDTO.setSong( songMapper.entityToDtoPure( comment.getSong() ) );
        commentDTO.setUserInfo( userInfoMapper.entityToDto( comment.getUserInfo() ) );

        return commentDTO;
    }

    @Override
    public Comment dtoToEntity(CommentDTO comment) {
        if ( comment == null ) {
            return null;
        }

        Comment comment1 = new Comment();

        comment1.setId( comment.getId() );
        comment1.setContent( comment.getContent() );
        comment1.setLocalDateTime( comment.getLocalDateTime() );
        comment1.setSong( songMapper.dtoToEntityPure( comment.getSong() ) );
        comment1.setUserInfo( userInfoMapper.dtoToEntity( comment.getUserInfo() ) );

        return comment1;
    }

    @Override
    public List<CommentDTO> entityToDtoList(List<Comment> comments) {
        if ( comments == null ) {
            return null;
        }

        List<CommentDTO> list = new ArrayList<CommentDTO>( comments.size() );
        for ( Comment comment : comments ) {
            list.add( entityToDto( comment ) );
        }

        return list;
    }

    @Override
    public List<Comment> dtoToEntityList(List<CommentDTO> comments) {
        if ( comments == null ) {
            return null;
        }

        List<Comment> list = new ArrayList<Comment>( comments.size() );
        for ( CommentDTO commentDTO : comments ) {
            list.add( dtoToEntity( commentDTO ) );
        }

        return list;
    }

    @Override
    public CommentDTO entityToDtoPure(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId( comment.getId() );
        commentDTO.setContent( comment.getContent() );
        commentDTO.setLocalDateTime( comment.getLocalDateTime() );
        commentDTO.setUserInfo( userInfoMapper.entityToDto( comment.getUserInfo() ) );

        return commentDTO;
    }

    @Override
    public Comment dtoToEntityPure(CommentDTO comment) {
        if ( comment == null ) {
            return null;
        }

        Comment comment1 = new Comment();

        comment1.setId( comment.getId() );
        comment1.setContent( comment.getContent() );
        comment1.setLocalDateTime( comment.getLocalDateTime() );
        comment1.setUserInfo( userInfoMapper.dtoToEntity( comment.getUserInfo() ) );

        return comment1;
    }

    @Override
    public List<CommentDTO> entityToDtoListPure(List<Comment> comments) {
        if ( comments == null ) {
            return null;
        }

        List<CommentDTO> list = new ArrayList<CommentDTO>( comments.size() );
        for ( Comment comment : comments ) {
            list.add( entityToDtoPure( comment ) );
        }

        return list;
    }

    @Override
    public List<Comment> dtoToEntityListPure(List<CommentDTO> comments) {
        if ( comments == null ) {
            return null;
        }

        List<Comment> list = new ArrayList<Comment>( comments.size() );
        for ( CommentDTO commentDTO : comments ) {
            list.add( dtoToEntityPure( commentDTO ) );
        }

        return list;
    }
}
