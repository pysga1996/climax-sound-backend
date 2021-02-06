package com.alpha.service.impl;

import com.alpha.model.dto.UserDTO;
import com.alpha.service.StorageService;
import org.springframework.stereotype.Service;

@Service
public class AvatarStorageService extends StorageService<UserDTO> {
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
