package com.alpha.mapper;

import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Theme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-03-18T14:27:58+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class ThemeMapperImpl extends ThemeMapper {

    @Autowired
    private SongMapper songMapper;

    @Override
    public ThemeDTO entityToDto(Theme theme) {
        if ( theme == null ) {
            return null;
        }

        ThemeDTO themeDTO = new ThemeDTO();

        themeDTO.setId( theme.getId() );
        themeDTO.setName( theme.getName() );
        themeDTO.setSongs( songCollectionToSongDTOCollection( theme.getSongs() ) );

        return themeDTO;
    }

    @Override
    public Theme dtoToEntity(ThemeDTO themeDTO) {
        if ( themeDTO == null ) {
            return null;
        }

        Theme theme = new Theme();

        theme.setId( themeDTO.getId() );
        theme.setName( themeDTO.getName() );
        theme.setSongs( songDTOCollectionToSongCollection( themeDTO.getSongs() ) );

        return theme;
    }

    @Override
    public List<ThemeDTO> entityToDtoList(List<Theme> themes) {
        if ( themes == null ) {
            return null;
        }

        List<ThemeDTO> list = new ArrayList<ThemeDTO>( themes.size() );
        for ( Theme theme : themes ) {
            list.add( entityToDto( theme ) );
        }

        return list;
    }

    @Override
    public List<Theme> dtoToEntityList(List<ThemeDTO> themes) {
        if ( themes == null ) {
            return null;
        }

        List<Theme> list = new ArrayList<Theme>( themes.size() );
        for ( ThemeDTO themeDTO : themes ) {
            list.add( dtoToEntity( themeDTO ) );
        }

        return list;
    }

    @Override
    public ThemeDTO entityToDtoPure(Theme theme) {
        if ( theme == null ) {
            return null;
        }

        ThemeDTO themeDTO = new ThemeDTO();

        themeDTO.setId( theme.getId() );
        themeDTO.setName( theme.getName() );

        return themeDTO;
    }

    @Override
    public Theme dtoToEntityPure(ThemeDTO theme) {
        if ( theme == null ) {
            return null;
        }

        Theme theme1 = new Theme();

        theme1.setId( theme.getId() );
        theme1.setName( theme.getName() );

        return theme1;
    }

    @Override
    public List<ThemeDTO> entityToDtoListPure(List<Theme> themes) {
        if ( themes == null ) {
            return null;
        }

        List<ThemeDTO> list = new ArrayList<ThemeDTO>( themes.size() );
        for ( Theme theme : themes ) {
            list.add( entityToDtoPure( theme ) );
        }

        return list;
    }

    @Override
    public List<Theme> dtoToEntityListPure(List<ThemeDTO> themes) {
        if ( themes == null ) {
            return null;
        }

        List<Theme> list = new ArrayList<Theme>( themes.size() );
        for ( ThemeDTO themeDTO : themes ) {
            list.add( dtoToEntityPure( themeDTO ) );
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
