package com.alpha.service.impl;

import com.alpha.model.dto.SongUploadForm;
import com.alpha.model.entity.*;
import com.alpha.service.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Service
public class FormConvertService {

    private final ArtistService artistService;

    private final GenreService genreService;

    private final TagService tagService;

    private final CountryService countryService;

    private final ThemeService themeService;

    public FormConvertService(ArtistService artistService, GenreService genreService, TagService tagService, CountryService countryService, ThemeService themeService) {
        this.artistService = artistService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.countryService = countryService;
        this.themeService = themeService;
    }

    public Collection<Artist> convertStringToArtistList(String string) {
        String[] artistsString = string.split(",");
        Collection<Artist> artistList = new HashSet<>();
        for (String artistString : artistsString) {
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

    public Collection<Genre> convertStringToGenreList(String string) {
        String[] genresString = string.split(",");
        Collection<Genre> genreList = new HashSet<>();
        for (String genreString : genresString) {
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

    public Collection<Tag> convertStringToTagList(String string) {
        String[] tagsString = string.split(",");
        Collection<Tag> tagList = new HashSet<>();
        for (String tagString : tagsString) {
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

    public Country convertStringToMood(String string) {
        Country checkedCountry = countryService.findByName(string);
        if (checkedCountry == null && !string.isEmpty()) {
            Country country = new Country(string);
            countryService.save(country);
            return country;
        }
        return null;
    }

    public Theme convertToActivity(String string) {
        Theme checkedTheme = themeService.findByName(string);
        if (checkedTheme == null && !string.isEmpty()) {
            Theme theme = new Theme(string);
            themeService.save(theme);
            return theme;
        }
        return null;
    }

    public Song convertSongUploadFormToSong(SongUploadForm songUploadForm) {
        Song song = new Song();
        song.setTitle(songUploadForm.getTitle());
        song.setCountry(songUploadForm.getCountry());
        song.setArtists(songUploadForm.getArtists());
        song.setDuration(songUploadForm.getDuration());
        song.setLyric(songUploadForm.getLyric());
        song.setReleaseDate(songUploadForm.getReleaseDate());
        song.setGenres(songUploadForm.getGenres());
        String[] tagsString = songUploadForm.getTags().split(",");
        List<Tag> tagList = new LinkedList<>();
        for (String tagString : tagsString) {
            tagList.add(new Tag(tagString.trim().toLowerCase()));
        }
        song.setTags(tagList);
        return song;
    }
}
