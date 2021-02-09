package com.alpha.model.util;

import com.alpha.model.dto.ArtistDTO;
import com.alpha.model.entity.Artist;

import java.util.Collection;

public abstract class UploadObject {

    public abstract String getUrl();

    public abstract String createFileName(String ext);

    public abstract String getFolder();

    public abstract String getBlobString();

    public abstract void setBlobString(String blobString);

    public String getArtistString(Collection<Artist> artists) {
        String artistsString = "";
        if (!artists.isEmpty()) {
            artistsString = " - ";
            for (Artist artist : artists) {
                artistsString = artistsString.concat(artist.getName()).concat("_");
            }
        }
        return artistsString;
    }

    public String getArtistDTOString(Collection<ArtistDTO> artists) {
        String artistsString = "";
        if (!artists.isEmpty()) {
            artistsString = " - ";
            for (ArtistDTO artist : artists) {
                artistsString = artistsString.concat(artist.getName()).concat("_");
            }
        }
        return artistsString;
    }
}
