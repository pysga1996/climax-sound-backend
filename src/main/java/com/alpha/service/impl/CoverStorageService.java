package com.alpha.service.impl;

import com.alpha.model.entity.Album;
import com.alpha.service.StorageService;
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
