package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty;
import com.alpha.error.FileNotFoundException;
import com.alpha.error.FileStorageException;
import com.alpha.model.util.UploadObject;
import com.alpha.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Log4j2
@Service
@Profile({"default"})
public class LocalStorageServiceImpl extends StorageService {

    private final Path storageLocation;

    @Autowired
    public LocalStorageServiceImpl(StorageProperty storageProperty) {
        this.storageLocation = Paths.get(storageProperty.getUploadDir())
                .toAbsolutePath().normalize();
        Path audioPath = storageLocation.resolve("audio");
        Path coverPath = storageLocation.resolve("cover");
        Path avatarPath = storageLocation.resolve("avatar");
        try {
            Files.createDirectories(this.storageLocation);
            Files.createDirectories(audioPath);
            Files.createDirectories(coverPath);
            Files.createDirectories(avatarPath);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String upload(MultipartFile multipartFile, UploadObject uploadObject) {
        String ext = getExtension(multipartFile);
        String folder = uploadObject.getFolder();
        String fileName = uploadObject.createFileName(ext);
        String rootUri = "/api/resource/download";
        this.normalizeFileName(fileName);
        try {
            // Check if the file's title contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            String blobString = folder + "/" + fileName;
            // Copy file to the target location (Replacing existing file with the same title)
            Path targetLocation = this.storageLocation.resolve(blobString);
            uploadObject.setBlobString(blobString);
            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(rootUri)
                    .path("/")
                    .path(folder)
                    .path("/")
                    .path(fileName).toUriString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public void delete(UploadObject uploadObject) {
        Path filePath = storageLocation.resolve(uploadObject.getBlobString()).normalize();
        File file = filePath.toFile();
        log.info("Delete file {} success? {}", uploadObject.getBlobString(), file.delete());
    }

    @Override
    public Resource loadFileAsResource(String fileName, String folder) {
        try {
            Path filePath = storageLocation.resolve(folder + "/" + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + folder + "/" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + folder + "/" + fileName, ex);
        }
    }
}
