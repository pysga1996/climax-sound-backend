package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Genre;
import com.alpha.model.entity.Song;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T00:30:21+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class GenreMapperImpl extends GenreMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public GenreDTO entityToDto(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        GenreDTO genreDTO = new GenreDTO();

        genreDTO.setId( genre.getId() );
        genreDTO.setName( genre.getName() );
        genreDTO.setSongs( songCollectionToSongDTOCollection( genre.getSongs() ) );
        genreDTO.setAlbums( albumCollectionToAlbumDTOCollection( genre.getAlbums() ) );

        return genreDTO;
    }

    @Override
    public Genre dtoToEntity(GenreDTO genre) {
        if ( genre == null ) {
            return null;
        }

        Genre genre1 = new Genre();

        genre1.setId( genre.getId() );
        genre1.setName( genre.getName() );
        genre1.setSongs( songDTOCollectionToSongCollection( genre.getSongs() ) );
        genre1.setAlbums( albumDTOCollectionToAlbumCollection( genre.getAlbums() ) );

        return genre1;
    }

    @Override
    public List<GenreDTO> entityToDtoList(List<Genre> genres) {
        if ( genres == null ) {
            return null;
        }

        List<GenreDTO> list = new ArrayList<GenreDTO>( genres.size() );
        for ( Genre genre : genres ) {
            list.add( entityToDto( genre ) );
        }

        return list;
    }

    @Override
    public List<Genre> dtoToEntityList(List<GenreDTO> genres) {
        if ( genres == null ) {
            return null;
        }

        List<Genre> list = new ArrayList<Genre>( genres.size() );
        for ( GenreDTO genreDTO : genres ) {
            list.add( dtoToEntity( genreDTO ) );
        }

        return list;
    }

    @Override
    public GenreDTO entityToDtoPure(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        GenreDTO genreDTO = new GenreDTO();

        genreDTO.setId( genre.getId() );
        genreDTO.setName( genre.getName() );

        return genreDTO;
    }

    @Override
    public Genre dtoToEntityPure(GenreDTO genre) {
        if ( genre == null ) {
            return null;
        }

        Genre genre1 = new Genre();

        genre1.setId( genre.getId() );
        genre1.setName( genre.getName() );

        return genre1;
    }

    @Override
    public List<GenreDTO> entityToDtoListPure(List<Genre> genres) {
        if ( genres == null ) {
            return null;
        }

        List<GenreDTO> list = new ArrayList<GenreDTO>( genres.size() );
        for ( Genre genre : genres ) {
            list.add( entityToDtoPure( genre ) );
        }

        return list;
    }

    @Override
    public List<Genre> dtoToEntityListPure(List<GenreDTO> genres) {
        if ( genres == null ) {
            return null;
        }

        List<Genre> list = new ArrayList<Genre>( genres.size() );
        for ( GenreDTO genreDTO : genres ) {
            list.add( dtoToEntityPure( genreDTO ) );
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
