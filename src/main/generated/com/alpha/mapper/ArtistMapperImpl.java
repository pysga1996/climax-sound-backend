package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Song;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T00:30:10+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class ArtistMapperImpl extends ArtistMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public ArtistDTO entityToDto(Artist artist) {
        if ( artist == null ) {
            return null;
        }

        ArtistDTO artistDTO = new ArtistDTO();

        artistDTO.setBlobString( artist.getBlobString() );
        artistDTO.setId( artist.getId() );
        artistDTO.setName( artist.getName() );
        artistDTO.setUnaccentName( artist.getUnaccentName() );
        artistDTO.setBirthDate( artist.getBirthDate() );
        artistDTO.setAvatarUrl( artist.getAvatarUrl() );
        artistDTO.setAvatarBlobString( artist.getAvatarBlobString() );
        artistDTO.setBiography( artist.getBiography() );
        artistDTO.setSongs( songCollectionToSongDTOCollection( artist.getSongs() ) );
        artistDTO.setAlbums( albumCollectionToAlbumDTOCollection( artist.getAlbums() ) );

        return artistDTO;
    }

    @Override
    public Artist dtoToEntity(ArtistDTO artistDTO) {
        if ( artistDTO == null ) {
            return null;
        }

        Artist artist = new Artist();

        artist.setBlobString( artistDTO.getBlobString() );
        artist.setId( artistDTO.getId() );
        artist.setName( artistDTO.getName() );
        artist.setUnaccentName( artistDTO.getUnaccentName() );
        artist.setBirthDate( artistDTO.getBirthDate() );
        artist.setAvatarUrl( artistDTO.getAvatarUrl() );
        artist.setAvatarBlobString( artistDTO.getAvatarBlobString() );
        artist.setBiography( artistDTO.getBiography() );
        artist.setSongs( songDTOCollectionToSongCollection( artistDTO.getSongs() ) );
        artist.setAlbums( albumDTOCollectionToAlbumCollection( artistDTO.getAlbums() ) );

        return artist;
    }

    @Override
    public List<ArtistDTO> entityToDtoList(List<Artist> artist) {
        if ( artist == null ) {
            return null;
        }

        List<ArtistDTO> list = new ArrayList<ArtistDTO>( artist.size() );
        for ( Artist artist1 : artist ) {
            list.add( entityToDto( artist1 ) );
        }

        return list;
    }

    @Override
    public List<Artist> dtoToEntityList(List<ArtistDTO> artistDTO) {
        if ( artistDTO == null ) {
            return null;
        }

        List<Artist> list = new ArrayList<Artist>( artistDTO.size() );
        for ( ArtistDTO artistDTO1 : artistDTO ) {
            list.add( dtoToEntity( artistDTO1 ) );
        }

        return list;
    }

    @Override
    public ArtistDTO entityToDtoPure(Artist artist) {
        if ( artist == null ) {
            return null;
        }

        ArtistDTO artistDTO = new ArtistDTO();

        artistDTO.setBlobString( artist.getBlobString() );
        artistDTO.setId( artist.getId() );
        artistDTO.setName( artist.getName() );
        artistDTO.setUnaccentName( artist.getUnaccentName() );
        artistDTO.setBirthDate( artist.getBirthDate() );
        artistDTO.setAvatarUrl( artist.getAvatarUrl() );
        artistDTO.setAvatarBlobString( artist.getAvatarBlobString() );
        artistDTO.setBiography( artist.getBiography() );

        return artistDTO;
    }

    @Override
    public Artist dtoToEntityPure(ArtistDTO artistDTO) {
        if ( artistDTO == null ) {
            return null;
        }

        Artist artist = new Artist();

        artist.setBlobString( artistDTO.getBlobString() );
        artist.setId( artistDTO.getId() );
        artist.setName( artistDTO.getName() );
        artist.setUnaccentName( artistDTO.getUnaccentName() );
        artist.setBirthDate( artistDTO.getBirthDate() );
        artist.setAvatarUrl( artistDTO.getAvatarUrl() );
        artist.setAvatarBlobString( artistDTO.getAvatarBlobString() );
        artist.setBiography( artistDTO.getBiography() );

        return artist;
    }

    @Override
    public List<ArtistDTO> entityToDtoListPure(List<Artist> artist) {
        if ( artist == null ) {
            return null;
        }

        List<ArtistDTO> list = new ArrayList<ArtistDTO>( artist.size() );
        for ( Artist artist1 : artist ) {
            list.add( entityToDtoPure( artist1 ) );
        }

        return list;
    }

    @Override
    public List<Artist> dtoToEntityListPure(List<ArtistDTO> artistDTO) {
        if ( artistDTO == null ) {
            return null;
        }

        List<Artist> list = new ArrayList<Artist>( artistDTO.size() );
        for ( ArtistDTO artistDTO1 : artistDTO ) {
            list.add( dtoToEntityPure( artistDTO1 ) );
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
