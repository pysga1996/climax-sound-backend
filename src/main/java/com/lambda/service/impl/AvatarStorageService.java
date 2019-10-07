package com.lambda.service.impl;

import com.lambda.exception.FileStorageException;
import com.lambda.model.entity.User;
import com.lambda.property.AvatarStorageProperties;
import com.lambda.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarStorageService extends StorageService<User> {
    final Path avatarStorageLocation;

    @Autowired
    public AvatarStorageService(AvatarStorageProperties avatarStorageLocation) {
        this.avatarStorageLocation = Paths.get(avatarStorageLocation.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.avatarStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
}
