package com.lambda.service.impl;

import com.lambda.exception.FileNotFoundException;
import com.lambda.exception.FileStorageException;
import com.lambda.model.entity.Album;
import com.lambda.model.entity.Artist;
import com.lambda.property.CoverStorageProperty;
import com.lambda.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

@Service
public class CoverStorageService implements StorageService<Album> {
    private final Path coverStorageLocation;

    @Autowired
    public CoverStorageService(CoverStorageProperty coverStorageProperty) {
        this.coverStorageLocation = Paths.get(coverStorageProperty.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.coverStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, Album album) {
        String originalFileName = file.getOriginalFilename();
        // Get file extension
        String extension = originalFileName!=null?originalFileName.substring(originalFileName.lastIndexOf(".") + 1):"";
        String avatarUrl = album.getCoverUrl();

        Collection<Artist> artists = album.getArtists();
        String artistsString = "";
        if (!artists.isEmpty()) {
            artistsString = " - ";
            for (Artist artist: artists) {
                artistsString = artistsString.concat(artist.getName()).concat("_");
            }
        }

        // check if new cover ext is different from old file ext
        if (avatarUrl != null && !avatarUrl.equals("")) {
            String oldExtension = avatarUrl.substring(avatarUrl.lastIndexOf(".") + 1);
            if (!oldExtension.equals(extension)) {
                String oldFileName = album.getId().toString().concat(" - ").concat(album.getName()).concat(artistsString).concat(".").concat(oldExtension);
                deleteFile(oldFileName);
            }
        }

        String fileName = StringUtils.cleanPath(album.getId().toString().concat(" - ").concat(album.getName()).concat(artistsString).concat(".").concat(extension));

        // Normalize file name
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.coverStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.coverStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public Boolean deleteFile(String fileName) {
        Path filePath = this.coverStorageLocation.resolve(fileName).normalize();
        File file = filePath.toFile();
        return file.delete();
    }
}
