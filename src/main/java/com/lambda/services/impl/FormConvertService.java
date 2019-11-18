package com.lambda.services.impl;

import com.lambda.models.entities.*;
import com.lambda.models.forms.UserForm;
import com.lambda.services.*;
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

    public User convertToUser(UserForm userForm, boolean createAction) {
        String username = userForm.getUsername();
        if (userService.findByUsername(username).isPresent() && createAction) return null;
        String password = passwordEncoder.encode(userForm.getPassword());
        String firstName = userForm.getFirstName();
        String lastName = userForm.getLastName();
        String phoneNumber = userForm.getPhoneNumber();
        Boolean gender = userForm.getGender();
        Date birthDate = userForm.getBirthDate();
        String email = userForm.getEmail();
        return new User(username, password, firstName, lastName, gender, birthDate, phoneNumber,  email);
    }
}
