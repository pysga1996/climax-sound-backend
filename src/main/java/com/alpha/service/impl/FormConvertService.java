package com.alpha.service.impl;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.dto.CountryDTO;
import com.alpha.model.dto.GenreDTO;
import com.alpha.model.dto.SongDTO;
import com.alpha.model.dto.SongUploadForm;
import com.alpha.model.dto.TagDTO;
import com.alpha.model.dto.ThemeDTO;
import com.alpha.service.ArtistService;
import com.alpha.service.CountryService;
import com.alpha.service.GenreService;
import com.alpha.service.TagService;
import com.alpha.service.ThemeService;
import java.io.IOException;
import java.util.ArrayList;
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

    public FormConvertService(ArtistService artistService, GenreService genreService,
        TagService tagService, CountryService countryService, ThemeService themeService) {
        this.artistService = artistService;
        this.genreService = genreService;
        this.tagService = tagService;
        this.countryService = countryService;
        this.themeService = themeService;
    }

    public Collection<ArtistDTO> convertStringToArtistList(String string) throws IOException {
        String[] artistsString = string.split(",");
        Collection<ArtistDTO> artistList = new HashSet<>();
        for (String artistString : artistsString) {
            if (!artistString.trim().isEmpty()) {
                ArtistDTO checkedArtist = artistService.findByName(artistString);
                if (checkedArtist == null) {
                    ArtistDTO artist = new ArtistDTO();
                    artist.setName(artistString.trim());
                    artistService.create(artist, null);
                    artistList.add(artist);
                } else {
                    artistList.add(checkedArtist);
                }
            }
        }
        if (artistList.isEmpty()) {
            return null;
        }
        return artistList;
    }

    public Collection<GenreDTO> convertStringToGenreList(String string) {
        String[] genresString = string.split(",");
        Collection<GenreDTO> genreList = new HashSet<>();
        for (String genreString : genresString) {
            if (!genreString.trim().isEmpty()) {
                GenreDTO checkedGenre = this.genreService.findByName(genreString);
                if (checkedGenre == null) {
                    GenreDTO genre = new GenreDTO();
                    genre.setName(genreString.toLowerCase().trim());
                    this.genreService.save(genre);
                    genreList.add(genre);
                } else {
                    genreList.add(checkedGenre);
                }
            }
        }
        if (genreList.isEmpty()) {
            return null;
        }
        return genreList;
    }

    public Collection<TagDTO> convertStringToTagList(String string) {
        String[] tagsString = string.split(",");
        Collection<TagDTO> tagList = new HashSet<>();
        for (String tagString : tagsString) {
            if (!tagString.trim().isEmpty()) {
                TagDTO checkedTag = this.tagService.findByName(tagString);
                if (checkedTag == null) {
                    TagDTO tag = new TagDTO();
                    tag.setName(tagString.toLowerCase().trim());
                    this.tagService.save(tag);
                    tagList.add(tag);
                } else {
                    tagList.add(checkedTag);
                }
            }
        }
        if (tagList.isEmpty()) {
            return null;
        }
        return tagList;
    }

    public CountryDTO convertStringToMood(String string) {
        CountryDTO checkedCountry = this.countryService.findByName(string);
        if (checkedCountry == null && !string.isEmpty()) {
            CountryDTO country = new CountryDTO();
            country.setName(string);
            this.countryService.save(country);
            return country;
        }
        return null;
    }

    public ThemeDTO convertToActivity(String string) {
        ThemeDTO checkedTheme = this.themeService.findByName(string);
        if (checkedTheme == null && !string.isEmpty()) {
            ThemeDTO theme = new ThemeDTO();
            theme.setName(string);
            this.themeService.save(theme);
            return theme;
        }
        return null;
    }

    public SongDTO convertSongUploadFormToSong(SongUploadForm songUploadForm) {
        SongDTO song = new SongDTO();
        song.setTitle(songUploadForm.getTitle());
        song.setCountry(songUploadForm.getCountry());
        song.setArtists(songUploadForm.getArtists());
        song.setDuration(songUploadForm.getDuration());
        song.setLyric(songUploadForm.getLyric());
        song.setReleaseDate(songUploadForm.getReleaseDate());
        song.setGenres(songUploadForm.getGenres());
        song.setTags(new ArrayList<>());
        if (songUploadForm.getTags() != null && !songUploadForm.getTags().trim().isEmpty()) {
            String[] tagsString = songUploadForm.getTags().split(",");
            for (String tagString : tagsString) {
                TagDTO tagDTO = new TagDTO();
                tagDTO.setName(tagString.trim().toLowerCase());
                song.getTags().add(tagDTO);
            }
        }
        return song;
    }
}
