package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Country;
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
public class CountryMapperImpl extends CountryMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private AlbumMapper albumMapper;

    @Override
    public CountryDTO entityToDto(Country country) {
        if ( country == null ) {
            return null;
        }

        CountryDTO countryDTO = new CountryDTO();

        countryDTO.setId( country.getId() );
        countryDTO.setName( country.getName() );
        countryDTO.setSongs( songCollectionToSongDTOCollection( country.getSongs() ) );
        countryDTO.setAlbums( albumCollectionToAlbumDTOCollection( country.getAlbums() ) );

        return countryDTO;
    }

    @Override
    public Country dtoToEntity(CountryDTO country) {
        if ( country == null ) {
            return null;
        }

        Country country1 = new Country();

        country1.setId( country.getId() );
        country1.setName( country.getName() );
        country1.setSongs( songDTOCollectionToSongCollection( country.getSongs() ) );
        country1.setAlbums( albumDTOCollectionToAlbumCollection( country.getAlbums() ) );

        return country1;
    }

    @Override
    public List<CountryDTO> entityToDtoList(List<Country> countries) {
        if ( countries == null ) {
            return null;
        }

        List<CountryDTO> list = new ArrayList<CountryDTO>( countries.size() );
        for ( Country country : countries ) {
            list.add( entityToDto( country ) );
        }

        return list;
    }

    @Override
    public List<Country> dtoToEntityList(List<CountryDTO> countries) {
        if ( countries == null ) {
            return null;
        }

        List<Country> list = new ArrayList<Country>( countries.size() );
        for ( CountryDTO countryDTO : countries ) {
            list.add( dtoToEntity( countryDTO ) );
        }

        return list;
    }

    @Override
    public CountryDTO entityToDtoPure(Country country) {
        if ( country == null ) {
            return null;
        }

        CountryDTO countryDTO = new CountryDTO();

        countryDTO.setId( country.getId() );
        countryDTO.setName( country.getName() );

        return countryDTO;
    }

    @Override
    public Country dtoToEntityPure(CountryDTO country) {
        if ( country == null ) {
            return null;
        }

        Country country1 = new Country();

        country1.setId( country.getId() );
        country1.setName( country.getName() );

        return country1;
    }

    @Override
    public List<CountryDTO> entityToDtoListPure(List<Country> countries) {
        if ( countries == null ) {
            return null;
        }

        List<CountryDTO> list = new ArrayList<CountryDTO>( countries.size() );
        for ( Country country : countries ) {
            list.add( entityToDtoPure( country ) );
        }

        return list;
    }

    @Override
    public List<Country> dtoToEntityListPure(List<CountryDTO> countries) {
        if ( countries == null ) {
            return null;
        }

        List<Country> list = new ArrayList<Country>( countries.size() );
        for ( CountryDTO countryDTO : countries ) {
            list.add( dtoToEntityPure( countryDTO ) );
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
