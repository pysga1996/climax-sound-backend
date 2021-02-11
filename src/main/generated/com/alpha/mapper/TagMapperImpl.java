package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T09:34:04+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class TagMapperImpl extends TagMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public TagDTO entityToDto(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();

        tagDTO.setId( tag.getId() );
        tagDTO.setName( tag.getName() );
        tagDTO.setSongs( songCollectionToSongDTOCollection( tag.getSongs() ) );
        tagDTO.setAlbums( albumCollectionToAlbumDTOCollection( tag.getAlbums() ) );

        return tagDTO;
    }

    @Override
    public Tag dtoToEntity(TagDTO tag) {
        if ( tag == null ) {
            return null;
        }

        Tag tag1 = new Tag();

        tag1.setId( tag.getId() );
        tag1.setName( tag.getName() );
        tag1.setSongs( songDTOCollectionToSongCollection( tag.getSongs() ) );
        tag1.setAlbums( albumDTOCollectionToAlbumCollection( tag.getAlbums() ) );

        return tag1;
    }

    @Override
    public List<TagDTO> entityToDtoList(List<Tag> tags) {
        if ( tags == null ) {
            return null;
        }

        List<TagDTO> list = new ArrayList<TagDTO>( tags.size() );
        for ( Tag tag : tags ) {
            list.add( entityToDto( tag ) );
        }

        return list;
    }

    @Override
    public List<Tag> dtoToEntityList(List<TagDTO> tags) {
        if ( tags == null ) {
            return null;
        }

        List<Tag> list = new ArrayList<Tag>( tags.size() );
        for ( TagDTO tagDTO : tags ) {
            list.add( dtoToEntity( tagDTO ) );
        }

        return list;
    }

    @Override
    public TagDTO entityToDtoPure(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();

        tagDTO.setId( tag.getId() );
        tagDTO.setName( tag.getName() );

        return tagDTO;
    }

    @Override
    public Tag dtoToEntityPure(TagDTO tag) {
        if ( tag == null ) {
            return null;
        }

        Tag tag1 = new Tag();

        tag1.setId( tag.getId() );
        tag1.setName( tag.getName() );

        return tag1;
    }

    @Override
    public List<TagDTO> entityToDtoListPure(List<Tag> tags) {
        if ( tags == null ) {
            return null;
        }

        List<TagDTO> list = new ArrayList<TagDTO>( tags.size() );
        for ( Tag tag : tags ) {
            list.add( entityToDtoPure( tag ) );
        }

        return list;
    }

    @Override
    public List<Tag> dtoToEntityListPure(List<TagDTO> tags) {
        if ( tags == null ) {
            return null;
        }

        List<Tag> list = new ArrayList<Tag>( tags.size() );
        for ( TagDTO tagDTO : tags ) {
            list.add( dtoToEntityPure( tagDTO ) );
        }

        return list;
    }

    protected Collection<SongDTO> songCollectionToSongDTOCollection(Collection<Song> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<SongDTO> collection1 = new ArrayList<SongDTO>( collection.size() );
        for ( Song song : collection ) {
            collection1.add( songMapper.entityToDtoPure( song ) );
        }

        return collection1;
    }

    protected Collection<AlbumDTO> albumCollectionToAlbumDTOCollection(Collection<Album> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<AlbumDTO> collection1 = new ArrayList<AlbumDTO>( collection.size() );
        for ( Album album : collection ) {
            collection1.add( albumMapper.entityToDtoPure( album ) );
        }

        return collection1;
    }

    protected Collection<Song> songDTOCollectionToSongCollection(Collection<SongDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Song> collection1 = new ArrayList<Song>( collection.size() );
        for ( SongDTO songDTO : collection ) {
            collection1.add( songMapper.dtoToEntityPure( songDTO ) );
        }

        return collection1;
    }

    protected Collection<Album> albumDTOCollectionToAlbumCollection(Collection<AlbumDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Album> collection1 = new ArrayList<Album>( collection.size() );
        for ( AlbumDTO albumDTO : collection ) {
            collection1.add( albumMapper.dtoToEntityPure( albumDTO ) );
        }

        return collection1;
    }
}
