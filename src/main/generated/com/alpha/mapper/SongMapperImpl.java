package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.PlaylistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Genre;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Song.SongBuilder;
import com.alpha.model.entity.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T09:34:05+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class SongMapperImpl extends SongMapper {

    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CountryMapper countryMapper;
    @Autowired
    private GenreMapper genreMapper;
    @Autowired
    private ThemeMapper themeMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private PlaylistMapper playlistMapper;

    @Override
    public SongDTO entityToDto(Song song) {
        if ( song == null ) {
            return null;
        }

        SongDTO songDTO = new SongDTO();

        songDTO.setId( song.getId() );
        songDTO.setTitle( song.getTitle() );
        songDTO.setUnaccentTitle( song.getUnaccentTitle() );
        songDTO.setReleaseDate( song.getReleaseDate() );
        songDTO.setUrl( song.getUrl() );
        songDTO.setComments( commentCollectionToCommentDTOCollection( song.getComments() ) );
        songDTO.setDisplayRating( song.getDisplayRating() );
        songDTO.setListeningFrequency( song.getListeningFrequency() );
        songDTO.setLiked( song.getLiked() );
        songDTO.setLyric( song.getLyric() );
        songDTO.setBlobString( song.getBlobString() );
        songDTO.setArtists( artistCollectionToArtistDTOCollection( song.getArtists() ) );
        songDTO.setAlbums( albumCollectionToAlbumDTOCollection( song.getAlbums() ) );
        songDTO.setTags( tagCollectionToTagDTOCollection( song.getTags() ) );
        songDTO.setGenres( genreCollectionToGenreDTOCollection( song.getGenres() ) );
        Collection<UserDTO> collection5 = song.getUsers();
        if ( collection5 != null ) {
            songDTO.setUsers( new ArrayList<UserDTO>( collection5 ) );
        }
        songDTO.setUploader( song.getUploader() );
        songDTO.setPlaylists( playlistCollectionToPlaylistDTOCollection( song.getPlaylists() ) );
        songDTO.setCountry( countryMapper.entityToDtoPure( song.getCountry() ) );
        songDTO.setTheme( themeMapper.entityToDtoPure( song.getTheme() ) );
        songDTO.setDuration( song.getDuration() );

        return songDTO;
    }

    @Override
    public Song dtoToEntity(SongDTO song) {
        if ( song == null ) {
            return null;
        }

        SongBuilder song1 = Song.builder();

        song1.id( song.getId() );
        song1.title( song.getTitle() );
        song1.unaccentTitle( song.getUnaccentTitle() );
        song1.releaseDate( song.getReleaseDate() );
        song1.url( song.getUrl() );
        song1.comments( commentDTOCollectionToCommentCollection( song.getComments() ) );
        song1.displayRating( song.getDisplayRating() );
        song1.listeningFrequency( song.getListeningFrequency() );
        song1.liked( song.getLiked() );
        song1.lyric( song.getLyric() );
        song1.blobString( song.getBlobString() );
        song1.artists( artistDTOCollectionToArtistCollection( song.getArtists() ) );
        song1.albums( albumDTOCollectionToAlbumCollection( song.getAlbums() ) );
        song1.tags( tagDTOCollectionToTagCollection( song.getTags() ) );
        song1.genres( genreDTOCollectionToGenreCollection( song.getGenres() ) );
        Collection<UserDTO> collection5 = song.getUsers();
        if ( collection5 != null ) {
            song1.users( new ArrayList<UserDTO>( collection5 ) );
        }
        song1.uploader( song.getUploader() );
        song1.playlists( playlistDTOCollectionToPlaylistCollection( song.getPlaylists() ) );
        song1.country( countryMapper.dtoToEntityPure( song.getCountry() ) );
        song1.theme( themeMapper.dtoToEntityPure( song.getTheme() ) );
        song1.duration( song.getDuration() );

        return song1.build();
    }

    @Override
    public List<SongDTO> entityToDtoList(List<Song> song) {
        if ( song == null ) {
            return null;
        }

        List<SongDTO> list = new ArrayList<SongDTO>( song.size() );
        for ( Song song1 : song ) {
            list.add( entityToDto( song1 ) );
        }

        return list;
    }

    @Override
    public List<Song> dtoToEntityList(List<SongDTO> songs) {
        if ( songs == null ) {
            return null;
        }

        List<Song> list = new ArrayList<Song>( songs.size() );
        for ( SongDTO songDTO : songs ) {
            list.add( dtoToEntity( songDTO ) );
        }

        return list;
    }

    @Override
    public SongDTO entityToDtoPure(Song song) {
        if ( song == null ) {
            return null;
        }

        SongDTO songDTO = new SongDTO();

        songDTO.setId( song.getId() );
        songDTO.setTitle( song.getTitle() );
        songDTO.setUnaccentTitle( song.getUnaccentTitle() );
        songDTO.setReleaseDate( song.getReleaseDate() );
        songDTO.setUrl( song.getUrl() );
        songDTO.setDisplayRating( song.getDisplayRating() );
        songDTO.setListeningFrequency( song.getListeningFrequency() );
        songDTO.setLiked( song.getLiked() );
        songDTO.setLyric( song.getLyric() );
        songDTO.setBlobString( song.getBlobString() );
        Collection<UserDTO> collection = song.getUsers();
        if ( collection != null ) {
            songDTO.setUsers( new ArrayList<UserDTO>( collection ) );
        }
        songDTO.setUploader( song.getUploader() );
        songDTO.setDuration( song.getDuration() );

        return songDTO;
    }

    @Override
    public Song dtoToEntityPure(SongDTO song) {
        if ( song == null ) {
            return null;
        }

        SongBuilder song1 = Song.builder();

        song1.id( song.getId() );
        song1.title( song.getTitle() );
        song1.unaccentTitle( song.getUnaccentTitle() );
        song1.releaseDate( song.getReleaseDate() );
        song1.url( song.getUrl() );
        song1.displayRating( song.getDisplayRating() );
        song1.listeningFrequency( song.getListeningFrequency() );
        song1.liked( song.getLiked() );
        song1.lyric( song.getLyric() );
        song1.blobString( song.getBlobString() );
        Collection<UserDTO> collection = song.getUsers();
        if ( collection != null ) {
            song1.users( new ArrayList<UserDTO>( collection ) );
        }
        song1.uploader( song.getUploader() );
        song1.duration( song.getDuration() );

        return song1.build();
    }

    @Override
    public List<SongDTO> entityToDtoListPure(List<Song> song) {
        if ( song == null ) {
            return null;
        }

        List<SongDTO> list = new ArrayList<SongDTO>( song.size() );
        for ( Song song1 : song ) {
            list.add( entityToDtoPure( song1 ) );
        }

        return list;
    }

    @Override
    public List<Song> dtoToEntityListPure(List<SongDTO> songs) {
        if ( songs == null ) {
            return null;
        }

        List<Song> list = new ArrayList<Song>( songs.size() );
        for ( SongDTO songDTO : songs ) {
            list.add( dtoToEntityPure( songDTO ) );
        }

        return list;
    }

    protected Collection<CommentDTO> commentCollectionToCommentDTOCollection(Collection<Comment> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<CommentDTO> collection1 = new ArrayList<CommentDTO>( collection.size() );
        for ( Comment comment : collection ) {
            collection1.add( commentMapper.entityToDtoPure( comment ) );
        }

        return collection1;
    }

    protected Collection<ArtistDTO> artistCollectionToArtistDTOCollection(Collection<Artist> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<ArtistDTO> collection1 = new ArrayList<ArtistDTO>( collection.size() );
        for ( Artist artist : collection ) {
            collection1.add( artistMapper.entityToDtoPure( artist ) );
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

    protected Collection<TagDTO> tagCollectionToTagDTOCollection(Collection<Tag> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<TagDTO> collection1 = new ArrayList<TagDTO>( collection.size() );
        for ( Tag tag : collection ) {
            collection1.add( tagMapper.entityToDtoPure( tag ) );
        }

        return collection1;
    }

    protected Collection<GenreDTO> genreCollectionToGenreDTOCollection(Collection<Genre> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<GenreDTO> collection1 = new ArrayList<GenreDTO>( collection.size() );
        for ( Genre genre : collection ) {
            collection1.add( genreMapper.entityToDtoPure( genre ) );
        }

        return collection1;
    }

    protected Collection<PlaylistDTO> playlistCollectionToPlaylistDTOCollection(Collection<Playlist> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<PlaylistDTO> collection1 = new ArrayList<PlaylistDTO>( collection.size() );
        for ( Playlist playlist : collection ) {
            collection1.add( playlistMapper.entityToDtoPure( playlist ) );
        }

        return collection1;
    }

    protected Collection<Comment> commentDTOCollectionToCommentCollection(Collection<CommentDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Comment> collection1 = new ArrayList<Comment>( collection.size() );
        for ( CommentDTO commentDTO : collection ) {
            collection1.add( commentMapper.dtoToEntityPure( commentDTO ) );
        }

        return collection1;
    }

    protected Collection<Artist> artistDTOCollectionToArtistCollection(Collection<ArtistDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Artist> collection1 = new ArrayList<Artist>( collection.size() );
        for ( ArtistDTO artistDTO : collection ) {
            collection1.add( artistMapper.dtoToEntityPure( artistDTO ) );
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

    protected Collection<Tag> tagDTOCollectionToTagCollection(Collection<TagDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Tag> collection1 = new ArrayList<Tag>( collection.size() );
        for ( TagDTO tagDTO : collection ) {
            collection1.add( tagMapper.dtoToEntityPure( tagDTO ) );
        }

        return collection1;
    }

    protected Collection<Genre> genreDTOCollectionToGenreCollection(Collection<GenreDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Genre> collection1 = new ArrayList<Genre>( collection.size() );
        for ( GenreDTO genreDTO : collection ) {
            collection1.add( genreMapper.dtoToEntityPure( genreDTO ) );
        }

        return collection1;
    }

    protected Collection<Playlist> playlistDTOCollectionToPlaylistCollection(Collection<PlaylistDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Playlist> collection1 = new ArrayList<Playlist>( collection.size() );
        for ( PlaylistDTO playlistDTO : collection ) {
            collection1.add( playlistMapper.dtoToEntityPure( playlistDTO ) );
        }

        return collection1;
    }
}
