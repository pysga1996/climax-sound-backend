package com.lambda.services.impl;

import com.lambda.models.entities.Album;
import com.lambda.services.StorageService;
import org.springframework.stereotype.Service;

@Service
public class CoverStorageService extends StorageService<Album> {
//    final Path coverStorageLocation;
//
//    @Autowired
//    public CoverStorageService(CoverStorageProperty coverStorageProperty) {
//        this.coverStorageLocation = Paths.get(coverStorageProperty.getUploadDir())
//                .toAbsolutePath().normalize();
//        try {
//            Files.createDirectories(this.coverStorageLocation);
//        } catch (Exception ex) {
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
}
