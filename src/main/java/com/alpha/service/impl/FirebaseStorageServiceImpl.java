package com.alpha.service.impl;

import com.alpha.error.FileStorageException;
import com.alpha.model.dto.UploadDTO;
import com.alpha.service.StorageService;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile({"default"})
public class FirebaseStorageServiceImpl extends StorageService {

    private final StorageClient storageClient;

    @Autowired
    public FirebaseStorageServiceImpl(StorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @Override
    public String upload(MultipartFile multipartFile, UploadDTO uploadDTO) throws IOException {
        String ext = this.getExtension(multipartFile);
        String fileName = uploadDTO.createFileName(ext);
        this.normalizeFileName(fileName);
        Bucket bucket = storageClient.bucket();
        try {
            InputStream fileInputStream = multipartFile.getInputStream();
            String blobString = uploadDTO.getFolder() + "/" + fileName;
            Blob blob = bucket.create(blobString, fileInputStream, Bucket.BlobWriteOption.userProject("climax-sound"));
            bucket.getStorage().updateAcl(blob.getBlobId(), Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            String blobName = blob.getName();
            uploadDTO.setBlobString(blobName);
            return blob.getMediaLink();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public void delete(UploadDTO uploadDTO) {
        String blobString = uploadDTO.getBlobString();
        BlobId blobId = BlobId.of(storageClient.bucket().getName(), blobString);
        storageClient.bucket().getStorage().delete(blobId);
    }
}
