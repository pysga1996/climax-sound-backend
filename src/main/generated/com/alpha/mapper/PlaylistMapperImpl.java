package com.alpha.mapper;

import com.alpha.model.dto.PlaylistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
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
public class PlaylistMapperImpl extends PlaylistMapper {

    @Autowired
    private SongMapper songMapper;

    @Override
    public PlaylistDTO entityToDto(Playlist playlist) {
        if ( playlist == null ) {
            return null;
        }

        PlaylistDTO playlistDTO = new PlaylistDTO();

        playlistDTO.setId( playlist.getId() );
        playlistDTO.setTitle( playlist.getTitle() );
        playlistDTO.setUserId( playlist.getUserId() );
        playlistDTO.setSongs( songCollectionToSongDTOCollection( playlist.getSongs() ) );

        return playlistDTO;
    }

    @Override
    public Playlist dtoToEntity(PlaylistDTO playlist) {
        if ( playlist == null ) {
            return null;
        }

        Playlist playlist1 = new Playlist();

        playlist1.setId( playlist.getId() );
        playlist1.setTitle( playlist.getTitle() );
        playlist1.setUserId( playlist.getUserId() );
        playlist1.setSongs( songDTOCollectionToSongCollection( playlist.getSongs() ) );

        return playlist1;
    }

    @Override
    public List<PlaylistDTO> entityToDtoList(List<Playlist> playlist) {
        if ( playlist == null ) {
            return null;
        }

        List<PlaylistDTO> list = new ArrayList<PlaylistDTO>( playlist.size() );
        for ( Playlist playlist1 : playlist ) {
            list.add( entityToDto( playlist1 ) );
        }

        return list;
    }

    @Override
    public List<Playlist> dtoToEntityList(List<PlaylistDTO> playlist) {
        if ( playlist == null ) {
            return null;
        }

        List<Playlist> list = new ArrayList<Playlist>( playlist.size() );
        for ( PlaylistDTO playlistDTO : playlist ) {
            list.add( dtoToEntity( playlistDTO ) );
        }

        return list;
    }

    @Override
    public PlaylistDTO entityToDtoPure(Playlist playlist) {
        if ( playlist == null ) {
            return null;
        }

        PlaylistDTO playlistDTO = new PlaylistDTO();

        playlistDTO.setId( playlist.getId() );
        playlistDTO.setTitle( playlist.getTitle() );
        playlistDTO.setUserId( playlist.getUserId() );

        return playlistDTO;
    }

    @Override
    public Playlist dtoToEntityPure(PlaylistDTO playlist) {
        if ( playlist == null ) {
            return null;
        }

        Playlist playlist1 = new Playlist();

        playlist1.setId( playlist.getId() );
        playlist1.setTitle( playlist.getTitle() );
        playlist1.setUserId( playlist.getUserId() );

        return playlist1;
    }

    @Override
    public List<PlaylistDTO> entityToDtoListPure(List<Playlist> playlist) {
        if ( playlist == null ) {
            return null;
        }

        List<PlaylistDTO> list = new ArrayList<PlaylistDTO>( playlist.size() );
        for ( Playlist playlist1 : playlist ) {
            list.add( entityToDtoPure( playlist1 ) );
        }

        return list;
    }

    @Override
    public List<Playlist> dtoToEntityListPure(List<PlaylistDTO> playlist) {
        if ( playlist == null ) {
            return null;
        }

        List<Playlist> list = new ArrayList<Playlist>( playlist.size() );
        for ( PlaylistDTO playlistDTO : playlist ) {
            list.add( dtoToEntityPure( playlistDTO ) );
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
}
