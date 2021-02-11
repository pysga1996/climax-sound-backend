package com.alpha.mapper;

import com.alpha.model.dto.AlbumDTO;
import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.CommentDTO;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.PlaylistDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.model.dto.UserDTO;
import com.alpha.model.entity.Album;
import com.alpha.model.entity.Artist;
import com.alpha.model.entity.Comment;
import com.alpha.model.entity.Country;
import com.alpha.model.entity.Genre;
import com.alpha.model.entity.Playlist;
import com.alpha.model.entity.Song;
import com.alpha.model.entity.Song.SongBuilder;
import com.alpha.model.entity.Tag;
import com.alpha.model.entity.Theme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T10:55:24+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class AlbumMapperImpl extends AlbumMapper {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private GenreMapper genreMapper;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public AlbumDTO entityToDto(Album album) {
        if ( album == null ) {
            return null;
        }

        AlbumDTO albumDTO = new AlbumDTO();

        albumDTO.setBlobString( album.getBlobString() );
        albumDTO.setId( album.getId() );
        albumDTO.setTitle( album.getTitle() );
        albumDTO.setReleaseDate( album.getReleaseDate() );
        albumDTO.setCoverUrl( album.getCoverUrl() );
        albumDTO.setCoverBlobString( album.getCoverBlobString() );
        albumDTO.setGenres( genreCollectionToGenreDTOCollection( album.getGenres() ) );
        albumDTO.setSongs( songCollectionToSongDTOCollection( album.getSongs() ) );
        albumDTO.setArtists( artistCollectionToArtistDTOCollection( album.getArtists() ) );
        albumDTO.setTags( tagCollectionToTagDTOCollection( album.getTags() ) );
        albumDTO.setCountry( countryToCountryDTO( album.getCountry() ) );
        albumDTO.setUploader( userInfoMapper.entityToDto( album.getUploader() ) );
        Collection<UserDTO> collection4 = album.getUsers();
        if ( collection4 != null ) {
            albumDTO.setUsers( new ArrayList<UserDTO>( collection4 ) );
        }

        return albumDTO;
    }

    @Override
    public Album dtoToEntity(AlbumDTO albums) {
        if ( albums == null ) {
            return null;
        }

        Album album = new Album();

        album.setBlobString( albums.getBlobString() );
        album.setId( albums.getId() );
        album.setTitle( albums.getTitle() );
        album.setReleaseDate( albums.getReleaseDate() );
        album.setCoverUrl( albums.getCoverUrl() );
        album.setCoverBlobString( albums.getCoverBlobString() );
        album.setGenres( genreDTOCollectionToGenreCollection( albums.getGenres() ) );
        album.setSongs( songDTOCollectionToSongCollection( albums.getSongs() ) );
        album.setArtists( artistDTOCollectionToArtistCollection( albums.getArtists() ) );
        album.setTags( tagDTOCollectionToTagCollection( albums.getTags() ) );
        album.setCountry( countryDTOToCountry( albums.getCountry() ) );
        album.setUploader( userInfoMapper.dtoToEntity( albums.getUploader() ) );
        Collection<UserDTO> collection4 = albums.getUsers();
        if ( collection4 != null ) {
            album.setUsers( new ArrayList<UserDTO>( collection4 ) );
        }

        return album;
    }

    @Override
    public List<AlbumDTO> entityToDtoList(List<Album> album) {
        if ( album == null ) {
            return null;
        }

        List<AlbumDTO> list = new ArrayList<AlbumDTO>( album.size() );
        for ( Album album1 : album ) {
            list.add( entityToDto( album1 ) );
        }

        return list;
    }

    @Override
    public List<Album> dtoToEntityList(List<AlbumDTO> albums) {
        if ( albums == null ) {
            return null;
        }

        List<Album> list = new ArrayList<Album>( albums.size() );
        for ( AlbumDTO albumDTO : albums ) {
            list.add( dtoToEntity( albumDTO ) );
        }

        return list;
    }

    @Override
    public AlbumDTO entityToDtoPure(Album album) {
        if ( album == null ) {
            return null;
        }

        AlbumDTO albumDTO = new AlbumDTO();

        albumDTO.setBlobString( album.getBlobString() );
        albumDTO.setId( album.getId() );
        albumDTO.setTitle( album.getTitle() );
        albumDTO.setReleaseDate( album.getReleaseDate() );
        albumDTO.setCoverUrl( album.getCoverUrl() );
        albumDTO.setCoverBlobString( album.getCoverBlobString() );
        albumDTO.setCountry( countryToCountryDTO( album.getCountry() ) );
        albumDTO.setUploader( userInfoMapper.entityToDto( album.getUploader() ) );
        Collection<UserDTO> collection = album.getUsers();
        if ( collection != null ) {
            albumDTO.setUsers( new ArrayList<UserDTO>( collection ) );
        }

        return albumDTO;
    }

    @Override
    public Album dtoToEntityPure(AlbumDTO album) {
        if ( album == null ) {
            return null;
        }

        Album album1 = new Album();

        album1.setBlobString( album.getBlobString() );
        album1.setId( album.getId() );
        album1.setTitle( album.getTitle() );
        album1.setReleaseDate( album.getReleaseDate() );
        album1.setCoverUrl( album.getCoverUrl() );
        album1.setCoverBlobString( album.getCoverBlobString() );
        album1.setCountry( countryDTOToCountry( album.getCountry() ) );
        album1.setUploader( userInfoMapper.dtoToEntity( album.getUploader() ) );
        Collection<UserDTO> collection = album.getUsers();
        if ( collection != null ) {
            album1.setUsers( new ArrayList<UserDTO>( collection ) );
        }

        return album1;
    }

    @Override
    public List<AlbumDTO> entityToDtoListPure(List<Album> albums) {
        if ( albums == null ) {
            return null;
        }

        List<AlbumDTO> list = new ArrayList<AlbumDTO>( albums.size() );
        for ( Album album : albums ) {
            list.add( entityToDtoPure( album ) );
        }

        return list;
    }

    @Override
    public List<Album> dtoToEntityListPure(List<AlbumDTO> albums) {
        if ( albums == null ) {
            return null;
        }

        List<Album> list = new ArrayList<Album>( albums.size() );
        for ( AlbumDTO albumDTO : albums ) {
            list.add( dtoToEntityPure( albumDTO ) );
        }

        return list;
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

    protected CommentDTO commentToCommentDTO(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId( comment.getId() );
        commentDTO.setContent( comment.getContent() );
        commentDTO.setLocalDateTime( comment.getLocalDateTime() );
        commentDTO.setSong( songToSongDTO( comment.getSong() ) );
        commentDTO.setUserInfo( userInfoMapper.entityToDto( comment.getUserInfo() ) );

        return commentDTO;
    }

    protected Collection<CommentDTO> commentCollectionToCommentDTOCollection(Collection<Comment> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<CommentDTO> collection1 = new ArrayList<CommentDTO>( collection.size() );
        for ( Comment comment : collection ) {
            collection1.add( commentToCommentDTO( comment ) );
        }

        return collection1;
    }

    protected Collection<SongDTO> songCollectionToSongDTOCollection1(Collection<Song> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<SongDTO> collection1 = new ArrayList<SongDTO>( collection.size() );
        for ( Song song : collection ) {
            collection1.add( songToSongDTO( song ) );
        }

        return collection1;
    }

    protected Collection<AlbumDTO> albumCollectionToAlbumDTOCollection(Collection<Album> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<AlbumDTO> collection1 = new ArrayList<AlbumDTO>( collection.size() );
        for ( Album album : collection ) {
            collection1.add( albumToAlbumDTO( album ) );
        }

        return collection1;
    }

    protected GenreDTO genreToGenreDTO(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        GenreDTO genreDTO = new GenreDTO();

        genreDTO.setId( genre.getId() );
        genreDTO.setName( genre.getName() );
        genreDTO.setSongs( songCollectionToSongDTOCollection1( genre.getSongs() ) );
        genreDTO.setAlbums( albumCollectionToAlbumDTOCollection( genre.getAlbums() ) );

        return genreDTO;
    }

    protected Collection<GenreDTO> genreCollectionToGenreDTOCollection1(Collection<Genre> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<GenreDTO> collection1 = new ArrayList<GenreDTO>( collection.size() );
        for ( Genre genre : collection ) {
            collection1.add( genreToGenreDTO( genre ) );
        }

        return collection1;
    }

    protected Collection<ArtistDTO> artistCollectionToArtistDTOCollection1(Collection<Artist> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<ArtistDTO> collection1 = new ArrayList<ArtistDTO>( collection.size() );
        for ( Artist artist : collection ) {
            collection1.add( artistToArtistDTO( artist ) );
        }

        return collection1;
    }

    protected TagDTO tagToTagDTO(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagDTO tagDTO = new TagDTO();

        tagDTO.setId( tag.getId() );
        tagDTO.setName( tag.getName() );
        tagDTO.setSongs( songCollectionToSongDTOCollection1( tag.getSongs() ) );
        tagDTO.setAlbums( albumCollectionToAlbumDTOCollection( tag.getAlbums() ) );

        return tagDTO;
    }

    protected Collection<TagDTO> tagCollectionToTagDTOCollection1(Collection<Tag> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<TagDTO> collection1 = new ArrayList<TagDTO>( collection.size() );
        for ( Tag tag : collection ) {
            collection1.add( tagToTagDTO( tag ) );
        }

        return collection1;
    }

    protected AlbumDTO albumToAlbumDTO(Album album) {
        if ( album == null ) {
            return null;
        }

        AlbumDTO albumDTO = new AlbumDTO();

        albumDTO.setBlobString( album.getBlobString() );
        albumDTO.setId( album.getId() );
        albumDTO.setTitle( album.getTitle() );
        albumDTO.setReleaseDate( album.getReleaseDate() );
        albumDTO.setCoverUrl( album.getCoverUrl() );
        albumDTO.setCoverBlobString( album.getCoverBlobString() );
        albumDTO.setGenres( genreCollectionToGenreDTOCollection1( album.getGenres() ) );
        albumDTO.setSongs( songCollectionToSongDTOCollection1( album.getSongs() ) );
        albumDTO.setArtists( artistCollectionToArtistDTOCollection1( album.getArtists() ) );
        albumDTO.setTags( tagCollectionToTagDTOCollection1( album.getTags() ) );
        albumDTO.setCountry( countryToCountryDTO( album.getCountry() ) );
        albumDTO.setUploader( userInfoMapper.entityToDto( album.getUploader() ) );
        Collection<UserDTO> collection4 = album.getUsers();
        if ( collection4 != null ) {
            albumDTO.setUsers( new ArrayList<UserDTO>( collection4 ) );
        }

        return albumDTO;
    }

    protected ArtistDTO artistToArtistDTO(Artist artist) {
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
        artistDTO.setSongs( songCollectionToSongDTOCollection1( artist.getSongs() ) );
        artistDTO.setAlbums( albumCollectionToAlbumDTOCollection( artist.getAlbums() ) );

        return artistDTO;
    }

    protected PlaylistDTO playlistToPlaylistDTO(Playlist playlist) {
        if ( playlist == null ) {
            return null;
        }

        PlaylistDTO playlistDTO = new PlaylistDTO();

        playlistDTO.setId( playlist.getId() );
        playlistDTO.setTitle( playlist.getTitle() );
        playlistDTO.setUserId( playlist.getUserId() );
        playlistDTO.setSongs( songCollectionToSongDTOCollection1( playlist.getSongs() ) );

        return playlistDTO;
    }

    protected Collection<PlaylistDTO> playlistCollectionToPlaylistDTOCollection(Collection<Playlist> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<PlaylistDTO> collection1 = new ArrayList<PlaylistDTO>( collection.size() );
        for ( Playlist playlist : collection ) {
            collection1.add( playlistToPlaylistDTO( playlist ) );
        }

        return collection1;
    }

    protected ThemeDTO themeToThemeDTO(Theme theme) {
        if ( theme == null ) {
            return null;
        }

        ThemeDTO themeDTO = new ThemeDTO();

        themeDTO.setId( theme.getId() );
        themeDTO.setName( theme.getName() );
        themeDTO.setSongs( songCollectionToSongDTOCollection1( theme.getSongs() ) );

        return themeDTO;
    }

    protected SongDTO songToSongDTO(Song song) {
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
        songDTO.setArtists( artistCollectionToArtistDTOCollection1( song.getArtists() ) );
        songDTO.setAlbums( albumCollectionToAlbumDTOCollection( song.getAlbums() ) );
        songDTO.setTags( tagCollectionToTagDTOCollection1( song.getTags() ) );
        songDTO.setGenres( genreCollectionToGenreDTOCollection1( song.getGenres() ) );
        Collection<UserDTO> collection5 = song.getUsers();
        if ( collection5 != null ) {
            songDTO.setUsers( new ArrayList<UserDTO>( collection5 ) );
        }
        songDTO.setUploader( song.getUploader() );
        songDTO.setPlaylists( playlistCollectionToPlaylistDTOCollection( song.getPlaylists() ) );
        songDTO.setCountry( countryToCountryDTO( song.getCountry() ) );
        songDTO.setTheme( themeToThemeDTO( song.getTheme() ) );
        songDTO.setDuration( song.getDuration() );

        return songDTO;
    }

    protected CountryDTO countryToCountryDTO(Country country) {
        if ( country == null ) {
            return null;
        }

        CountryDTO countryDTO = new CountryDTO();

        countryDTO.setId( country.getId() );
        countryDTO.setName( country.getName() );
        countryDTO.setSongs( songCollectionToSongDTOCollection1( country.getSongs() ) );
        countryDTO.setAlbums( albumCollectionToAlbumDTOCollection( country.getAlbums() ) );

        return countryDTO;
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

    protected Comment commentDTOToComment(CommentDTO commentDTO) {
        if ( commentDTO == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setId( commentDTO.getId() );
        comment.setContent( commentDTO.getContent() );
        comment.setLocalDateTime( commentDTO.getLocalDateTime() );
        comment.setSong( songDTOToSong( commentDTO.getSong() ) );
        comment.setUserInfo( userInfoMapper.dtoToEntity( commentDTO.getUserInfo() ) );

        return comment;
    }

    protected Collection<Comment> commentDTOCollectionToCommentCollection(Collection<CommentDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Comment> collection1 = new ArrayList<Comment>( collection.size() );
        for ( CommentDTO commentDTO : collection ) {
            collection1.add( commentDTOToComment( commentDTO ) );
        }

        return collection1;
    }

    protected Collection<Song> songDTOCollectionToSongCollection1(Collection<SongDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Song> collection1 = new ArrayList<Song>( collection.size() );
        for ( SongDTO songDTO : collection ) {
            collection1.add( songDTOToSong( songDTO ) );
        }

        return collection1;
    }

    protected Collection<Album> albumDTOCollectionToAlbumCollection(Collection<AlbumDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Album> collection1 = new ArrayList<Album>( collection.size() );
        for ( AlbumDTO albumDTO : collection ) {
            collection1.add( albumDTOToAlbum( albumDTO ) );
        }

        return collection1;
    }

    protected Genre genreDTOToGenre(GenreDTO genreDTO) {
        if ( genreDTO == null ) {
            return null;
        }

        Genre genre = new Genre();

        genre.setId( genreDTO.getId() );
        genre.setName( genreDTO.getName() );
        genre.setSongs( songDTOCollectionToSongCollection1( genreDTO.getSongs() ) );
        genre.setAlbums( albumDTOCollectionToAlbumCollection( genreDTO.getAlbums() ) );

        return genre;
    }

    protected Collection<Genre> genreDTOCollectionToGenreCollection1(Collection<GenreDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Genre> collection1 = new ArrayList<Genre>( collection.size() );
        for ( GenreDTO genreDTO : collection ) {
            collection1.add( genreDTOToGenre( genreDTO ) );
        }

        return collection1;
    }

    protected Collection<Artist> artistDTOCollectionToArtistCollection1(Collection<ArtistDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Artist> collection1 = new ArrayList<Artist>( collection.size() );
        for ( ArtistDTO artistDTO : collection ) {
            collection1.add( artistDTOToArtist( artistDTO ) );
        }

        return collection1;
    }

    protected Tag tagDTOToTag(TagDTO tagDTO) {
        if ( tagDTO == null ) {
            return null;
        }

        Tag tag = new Tag();

        tag.setId( tagDTO.getId() );
        tag.setName( tagDTO.getName() );
        tag.setSongs( songDTOCollectionToSongCollection1( tagDTO.getSongs() ) );
        tag.setAlbums( albumDTOCollectionToAlbumCollection( tagDTO.getAlbums() ) );

        return tag;
    }

    protected Collection<Tag> tagDTOCollectionToTagCollection1(Collection<TagDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Tag> collection1 = new ArrayList<Tag>( collection.size() );
        for ( TagDTO tagDTO : collection ) {
            collection1.add( tagDTOToTag( tagDTO ) );
        }

        return collection1;
    }

    protected Album albumDTOToAlbum(AlbumDTO albumDTO) {
        if ( albumDTO == null ) {
            return null;
        }

        Album album = new Album();

        album.setBlobString( albumDTO.getBlobString() );
        album.setId( albumDTO.getId() );
        album.setTitle( albumDTO.getTitle() );
        album.setReleaseDate( albumDTO.getReleaseDate() );
        album.setCoverUrl( albumDTO.getCoverUrl() );
        album.setCoverBlobString( albumDTO.getCoverBlobString() );
        album.setGenres( genreDTOCollectionToGenreCollection1( albumDTO.getGenres() ) );
        album.setSongs( songDTOCollectionToSongCollection1( albumDTO.getSongs() ) );
        album.setArtists( artistDTOCollectionToArtistCollection1( albumDTO.getArtists() ) );
        album.setTags( tagDTOCollectionToTagCollection1( albumDTO.getTags() ) );
        album.setCountry( countryDTOToCountry( albumDTO.getCountry() ) );
        album.setUploader( userInfoMapper.dtoToEntity( albumDTO.getUploader() ) );
        Collection<UserDTO> collection4 = albumDTO.getUsers();
        if ( collection4 != null ) {
            album.setUsers( new ArrayList<UserDTO>( collection4 ) );
        }

        return album;
    }

    protected Artist artistDTOToArtist(ArtistDTO artistDTO) {
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
        artist.setSongs( songDTOCollectionToSongCollection1( artistDTO.getSongs() ) );
        artist.setAlbums( albumDTOCollectionToAlbumCollection( artistDTO.getAlbums() ) );

        return artist;
    }

    protected Playlist playlistDTOToPlaylist(PlaylistDTO playlistDTO) {
        if ( playlistDTO == null ) {
            return null;
        }

        Playlist playlist = new Playlist();

        playlist.setId( playlistDTO.getId() );
        playlist.setTitle( playlistDTO.getTitle() );
        playlist.setUserId( playlistDTO.getUserId() );
        playlist.setSongs( songDTOCollectionToSongCollection1( playlistDTO.getSongs() ) );

        return playlist;
    }

    protected Collection<Playlist> playlistDTOCollectionToPlaylistCollection(Collection<PlaylistDTO> collection) {
        if ( collection == null ) {
            return null;
        }

        Collection<Playlist> collection1 = new ArrayList<Playlist>( collection.size() );
        for ( PlaylistDTO playlistDTO : collection ) {
            collection1.add( playlistDTOToPlaylist( playlistDTO ) );
        }

        return collection1;
    }

    protected Theme themeDTOToTheme(ThemeDTO themeDTO) {
        if ( themeDTO == null ) {
            return null;
        }

        Theme theme = new Theme();

        theme.setId( themeDTO.getId() );
        theme.setName( themeDTO.getName() );
        theme.setSongs( songDTOCollectionToSongCollection1( themeDTO.getSongs() ) );

        return theme;
    }

    protected Song songDTOToSong(SongDTO songDTO) {
        if ( songDTO == null ) {
            return null;
        }

        SongBuilder song = Song.builder();

        song.id( songDTO.getId() );
        song.title( songDTO.getTitle() );
        song.unaccentTitle( songDTO.getUnaccentTitle() );
        song.releaseDate( songDTO.getReleaseDate() );
        song.url( songDTO.getUrl() );
        song.comments( commentDTOCollectionToCommentCollection( songDTO.getComments() ) );
        song.displayRating( songDTO.getDisplayRating() );
        song.listeningFrequency( songDTO.getListeningFrequency() );
        song.liked( songDTO.getLiked() );
        song.lyric( songDTO.getLyric() );
        song.blobString( songDTO.getBlobString() );
        song.artists( artistDTOCollectionToArtistCollection1( songDTO.getArtists() ) );
        song.albums( albumDTOCollectionToAlbumCollection( songDTO.getAlbums() ) );
        song.tags( tagDTOCollectionToTagCollection1( songDTO.getTags() ) );
        song.genres( genreDTOCollectionToGenreCollection1( songDTO.getGenres() ) );
        Collection<UserDTO> collection5 = songDTO.getUsers();
        if ( collection5 != null ) {
            song.users( new ArrayList<UserDTO>( collection5 ) );
        }
        song.uploader( songDTO.getUploader() );
        song.playlists( playlistDTOCollectionToPlaylistCollection( songDTO.getPlaylists() ) );
        song.country( countryDTOToCountry( songDTO.getCountry() ) );
        song.theme( themeDTOToTheme( songDTO.getTheme() ) );
        song.duration( songDTO.getDuration() );

        return song.build();
    }

    protected Country countryDTOToCountry(CountryDTO countryDTO) {
        if ( countryDTO == null ) {
            return null;
        }

        Country country = new Country();

        country.setId( countryDTO.getId() );
        country.setName( countryDTO.getName() );
        country.setSongs( songDTOCollectionToSongCollection1( countryDTO.getSongs() ) );
        country.setAlbums( albumDTOCollectionToAlbumCollection( countryDTO.getAlbums() ) );

        return country;
    }
}
