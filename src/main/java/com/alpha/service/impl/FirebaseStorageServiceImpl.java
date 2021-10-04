package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.Status;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.StorageService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@DependsOn("firebaseStorage")
@ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "firebase")
public class FirebaseStorageServiceImpl extends StorageService {

    private final StorageClient storageClient;

    private final ResourceInfoRepository resourceInfoRepository;

    @Getter
    @Setter
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public FirebaseStorageServiceImpl(StorageClient storageClient,
        ResourceInfoRepository resourceInfoRepository) {
        this.storageClient = storageClient;
        this.resourceInfoRepository = resourceInfoRepository;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.FIREBASE;
    }

    @Override
    @SneakyThrows
    public ResourceInfo upload(MultipartFile multipartFile, Media media) {
        ResourceInfo resourceInfo = media.generateResource(multipartFile);
        Bucket bucket = storageClient.bucket();
        try {
            this.deleteOldResources(resourceInfo, StorageType.FIREBASE);
            InputStream fileInputStream = multipartFile.getInputStream();
            String blobString = resourceInfo.getFolder() + "/" + resourceInfo.getFileName();
            Blob blob = bucket.create(blobString, fileInputStream,
                Bucket.BlobWriteOption.userProject("climax-sound"));
            bucket.getStorage()
                .updateAcl(blob.getBlobId(), Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            String blobName = blob.getName();
            String mediaLink = blob.getMediaLink();
            resourceInfo.setStoragePath(blobName);
            resourceInfo.setUri(mediaLink);
            resourceInfo.setStorageType(StorageType.FIREBASE);
            resourceInfo.setStatus(Status.ACTIVE);
            this.saveResourceInfo(resourceInfo, StorageType.FIREBASE);
            return resourceInfo;
        } catch (IOException ex) {
            log.error("Could not store file {}. Please try again!",
                resourceInfo.getFileName(), ex);
            throw ex;
        }
    }

    @Override
    public void delete(ResourceInfo resourceInfo) {
        String blobName = resourceInfo.getStoragePath();
        BlobId blobId = BlobId.of(storageClient.bucket().getName(), blobName);
        storageClient.bucket().getStorage().delete(blobId);
    }

    @Override
    public ResourceInfoRepository getResourceInfoRepository() {
        return resourceInfoRepository;
    }
}
