package com.lambda.service.impl;

import com.lambda.model.entity.*;
import com.lambda.model.form.AlbumForm;
import com.lambda.model.form.AudioUploadForm;
import com.lambda.model.form.MediaForm;
import com.lambda.model.form.UserForm;
import com.lambda.model.util.MediaObject;
import com.lambda.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FormConvertService {
    @Autowired
    SongService songService;

    @Autowired
    AlbumService albumService;

    @Autowired
    ArtistService artistService;

    @Autowired
    GenreService genreService;

    @Autowired
    TagService tagService;

    @Autowired
    CountryService countryService;

    @Autowired
    ThemeService themeService;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Collection<Artist> convertStringToArtistList(String string) {
        String[] artistsString = string.split(",");
        Collection<Artist> artistList = new HashSet<>();
        for (String artistString: artistsString) {
            if (!artistString.trim().isEmpty()) {
                Artist checkedArtist = artistService.findByName(artistString);
                if (checkedArtist == null) {
                    Artist artist = new Artist(artistString.trim());
                    artistService.save(artist);
                    artistList.add(artist);
                } else {
                    artistList.add(checkedArtist);
                }
            }
        }
        if (artistList.isEmpty()) return null;
        return artistList;
    }

    private Collection<Genre> convertStringToGenreList(String string) {
        String[] genresString = string.split(",");
        Collection<Genre> genreList = new HashSet<>();
        for (String genreString: genresString) {
            if (!genreString.trim().isEmpty()) {
                Genre checkedGenre = genreService.findByName(genreString);
                if (checkedGenre == null) {
                    Genre genre = new Genre(genreString.toLowerCase().trim());
                    genreService.save(genre);
                    genreList.add(genre);
                } else {
                    genreList.add(checkedGenre);
                }
            }
        }
        if (genreList.isEmpty()) return null;
        return genreList;
    }

    private Collection<Tag> convertStringToTagList(String string) {
        String[] tagsString = string.split(",");
        Collection<Tag> tagList = new HashSet<>();
        for (String tagString: tagsString) {
            if (!tagString.trim().isEmpty()) {
                Tag checkedTag = tagService.findByName(tagString);
                if (checkedTag == null) {
                    Tag tag = new Tag(tagString.toLowerCase().trim());
                    tagService.save(tag);
                    tagList.add(tag);
                } else {
                    tagList.add(checkedTag);
                }
            }
        }
        if (tagList.isEmpty()) return null;
        return tagList;
    }

    private Country convertStringToMood(String string) {
        Country checkedCountry = countryService.findByName(string);
        if (checkedCountry == null && !string.isEmpty()) {
            Country country = new Country(string);
            countryService.save(country);
            return country;
        }
        return null;
    }

    private Theme convertToActivity(String string) {
        Theme checkedTheme = themeService.findByName(string);
        if (checkedTheme == null && !string.isEmpty()) {
            Theme theme = new Theme(string);
            themeService.save(theme);
            return theme;
        }
        return null;
    }

    private Boolean checkSongExistence(AudioUploadForm audioUploadForm) {
        Iterable<Song> checkedSongs = songService.findAllByName(audioUploadForm.getName());
        boolean isExisted = false;
        for (Song checkedSong: checkedSongs) {
            if (compareTwoArtistSet(checkedSong.getArtists(), audioUploadForm.getArtists().split(","))) {
                isExisted = true;
                break;
            }
        }
        return isExisted;
    }

    private Boolean checkAlbumExistence(AlbumForm albumForm) {
        Iterable<Album> checkedAlbums = albumService.findByName(albumForm.getName());
        boolean isExisted = false;
        for (Album checkedAlbum: checkedAlbums) {
            if (compareTwoArtistSet(checkedAlbum.getArtists(), albumForm.getArtists().split(","))) {
                isExisted = true;
                break;
            }
        }
        return isExisted;
    }

    private boolean compareTwoArtistSet(Collection<Artist> checkedArtistCollection, String[] artistStringArray) {
        Set<Artist> checkedArtistSet = new HashSet<>(checkedArtistCollection);
        Set<String> checkedArtistStringSet = new HashSet<>();
        for (Artist artist: checkedArtistSet) {
            checkedArtistStringSet.add(artist.getName());
        }
        Set<String> artistStringSet = new HashSet<>(Arrays.asList(artistStringArray));
        return checkedArtistStringSet.equals(artistStringSet);
    }

    public Song convertToSong(AudioUploadForm audioUploadForm) {
        if (checkSongExistence(audioUploadForm)) return null;
        String songName = audioUploadForm.getName();
        Date releaseDate = audioUploadForm.getReleaseDate();
        Song song = new Song(songName, releaseDate);
        setFields(song, audioUploadForm);
        return song;
    }

    public Album convertToAlbum(AlbumForm albumForm) {
        if (checkAlbumExistence(albumForm)) return null;
        String songName = albumForm.getName();
        Date releaseDate = albumForm.getReleaseDate();
        Album album = new Album(songName, releaseDate);
        setFields(album, albumForm);
        return album;
    }

    private void setFields(MediaObject mediaObject, MediaForm mediaForm) {
        Collection<Artist> artistsList = convertStringToArtistList(mediaForm.getArtists());
        mediaObject.setArtists(artistsList);
        Collection<Genre> genreList = convertStringToGenreList(mediaForm.getGenres());
        mediaObject.setGenres(genreList);
        Collection<Tag> tagList = convertStringToTagList(mediaForm.getTags());
        mediaObject.setTags(tagList);
        Country country = convertStringToMood(mediaForm.getCountry().trim());
        mediaObject.setCountry(country);
        Theme theme = convertToActivity(mediaForm.getCountry().toLowerCase().trim());
        mediaObject.setTheme(theme);
    }

    public User convertToUser(UserForm userForm, boolean createAction) {
        String username = userForm.getUsername();
        if (userService.findByUsername(username) != null && createAction) return null;
        String password = passwordEncoder.encode(userForm.getPassword());
        String firstName = userForm.getFirstName();
        String lastName = userForm.getLastName();
        String phoneNumber = userForm.getPhoneNumber();
        Boolean gender = userForm.getGender();
        Date birthDate = userForm.getBirthDate();
        String email = userForm.getEmail();
        return new User(username, password, firstName, lastName, phoneNumber, gender, birthDate, email);
    }
}
