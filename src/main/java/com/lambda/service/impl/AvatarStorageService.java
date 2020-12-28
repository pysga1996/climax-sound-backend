package com.lambda.service.impl;

import com.lambda.model.entities.User;
import com.lambda.service.StorageService;
import org.springframework.stereotype.Service;

@Service
public class AvatarStorageService extends StorageService<User> {
//    final Path avatarStorageLocation;
//
//    @Autowired
//    public AvatarStorageService(AvatarStorageProperties avatarStorageLocation) {
//        this.avatarStorageLocation = Paths.get(avatarStorageLocation.getUploadDir())
//                .toAbsolutePath().normalize();
//
//        try {
//            Files.createDirectories(this.avatarStorageLocation);
//        } catch (Exception ex) {
//            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
//        }
//    }
}
