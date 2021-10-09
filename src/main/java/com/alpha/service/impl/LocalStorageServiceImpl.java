package com.alpha.service.impl;

import com.alpha.config.properties.LocalStorageProperty;
import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.ModelStatus;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.StorageService;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Service
@DependsOn("localStorageProperty")
@ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "local")
public class LocalStorageServiceImpl extends StorageService {

    private final Path storageLocation;

    private final ResourceInfoRepository resourceInfoRepository;

    @Value("${storage.local.upload-root-uri}")
    private String rootUri;

    @Getter
    @Setter
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    @SneakyThrows
    public LocalStorageServiceImpl(LocalStorageProperty localStorageProperty,
        ResourceInfoRepository resourceInfoRepository) {
        this.storageLocation = Paths.get(localStorageProperty.getUploadDir())
            .toAbsolutePath().normalize();
        this.resourceInfoRepository = resourceInfoRepository;
        Path audioPath = storageLocation.resolve("audio");
        Path coverPath = storageLocation.resolve("cover");
        Path avatarPath = storageLocation.resolve("avatar");
        try {
            Files.createDirectories(this.storageLocation);
            Files.createDirectories(audioPath);
            Files.createDirectories(coverPath);
            Files.createDirectories(avatarPath);
        } catch (IOException ex) {
            log.error("Could not create the directory where the uploaded files will be stored: ",
                ex);
            throw ex;
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.LOCAL;
    }

    @Override
    public ResourceInfo upload(MultipartFile multipartFile, Media media) {
        ResourceInfo resourceInfo = media.generateResource(multipartFile);
        try {
            this.deleteOldResources(resourceInfo, StorageType.LOCAL);
            String blobName = resourceInfo.getFolder() + "/" + resourceInfo.getFileName();
            // Copy file to the target location (Replacing existing file with the same title)
            Path targetLocation = this.storageLocation.resolve(blobName);
            resourceInfo.setStoragePath(blobName);
            long bytes = Files.copy(multipartFile.getInputStream(), targetLocation,
                StandardCopyOption.REPLACE_EXISTING);
            log.info("{} bytes copied", bytes);
//              ServletUriComponentsBuilder.fromCurrentContextPath()
            String uri = UriComponentsBuilder.newInstance()
                .path(this.rootUri)
                .path("/")
                .path(resourceInfo.getFolder())
                .path("/")
                .path(resourceInfo.getFileName()).toUriString();
            resourceInfo.setUri(uri);
            resourceInfo.setStorageType(StorageType.LOCAL);
            resourceInfo.setStatus(ModelStatus.ACTIVE);
            this.saveResourceInfo(resourceInfo, StorageType.LOCAL);
            return resourceInfo;
        } catch (IOException ex) {
            log.error("Could not store file {}. Please try again!", resourceInfo.getFileName(), ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(ResourceInfo resourceInfo) {
        Path filePath = storageLocation.resolve(resourceInfo.getStoragePath()).normalize();
        File file = filePath.toFile();
        log.info("Delete file {} success? {}", resourceInfo.getStoragePath(), file.delete());
    }

    @Override
    public Resource loadFileAsResource(String fileName, String folder) {
        try {
            Path filePath = storageLocation.resolve(folder + "/" + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new EntityNotFoundException("File not found " + folder + "/" + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("Invalid uri: ", ex);
            throw new EntityNotFoundException("File not found " + folder + "/" + fileName);
        }
    }

    @Override
    public ResourceInfoRepository getResourceInfoRepository() {
        return resourceInfoRepository;
    }
}
