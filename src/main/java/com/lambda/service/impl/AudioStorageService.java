package com.lambda.service.impl;

import com.lambda.exception.FileStorageException;
import com.lambda.model.entity.Song;
import com.lambda.property.AudioStorageProperties;
import com.lambda.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AudioStorageService extends StorageService<Song> {
//    final Path audioStorageLocation;

//    @Autowired
//    public AudioStorageService(AudioStorageProperties audioStorageProperties) {
//        this.audioStorageLocation = Paths.get(audioStorageProperties.getUploadDir())
//                .toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(this.audioStorageLocation);
//        } catch (Exception ex) {
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
}
