package com.alpha.model.entity;

import java.util.Collection;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public abstract class Media {

    public String createFilenameFromArtists(Long id, String title, Collection<Artist> artists,
        String ext) {
        String artistsString = "";
        if (!artists.isEmpty()) {
            artistsString = " - ";
            for (Artist artist : artists) {
                artistsString = artistsString.concat(artist.getName()).concat("_");
            }
        }
        return StringUtils.cleanPath(String.valueOf(id).concat(" - ")
            .concat(title).concat(artistsString).concat(".").concat(ext));
    }

    protected String getExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName != null ?
            originalFileName.substring(originalFileName.lastIndexOf(".") + 1) : "";
    }

    protected String normalizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9.\\-]", "_").toLowerCase();
    }

    public abstract ResourceInfo generateResource(MultipartFile file);
}
