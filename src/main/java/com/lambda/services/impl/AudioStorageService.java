package com.lambda.services.impl;

import com.lambda.models.entities.Song;
import com.lambda.services.StorageService;
import org.springframework.stereotype.Service;

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
