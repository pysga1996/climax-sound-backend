package com.lambda.service.impl;

import com.lambda.exception.FileNotFoundException;
import com.lambda.exception.FileStorageException;
import com.lambda.property.ImageStorageProperties;
import com.lambda.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageStorageService implements StorageService<String> {
    private final Path imageStorageLocation;

    @Autowired
    public ImageStorageService(ImageStorageProperties imageStorageLocation) {
        this.imageStorageLocation = Paths.get(imageStorageLocation.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String username) {
        String originalFileName = file.getOriginalFilename();
        // Get file extension
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        // Normalize file name
        String fileName = StringUtils.cleanPath(username).concat(".").concat(extension);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.imageStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.imageStorageLocation.resolve(fileName ).normalize();
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
}
