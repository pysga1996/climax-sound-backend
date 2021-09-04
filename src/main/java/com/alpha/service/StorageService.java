package com.alpha.service;

import com.alpha.error.FileStorageException;
import com.alpha.model.dto.UploadDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public abstract class StorageService {

    protected abstract ResourceInfoRepository getResourceInfoRepository();

    public abstract ResourceInfo upload(MultipartFile multipartFile, Media media)
        throws IOException;

    public ResourceInfo upload(MultipartFile multipartFile, Media media,
        ResourceInfo oldResourceInfo)
        throws IOException {
        ResourceInfo newResourceInfo = this.upload(multipartFile, media);
        if (oldResourceInfo != null) {
            this.delete(oldResourceInfo);
            oldResourceInfo.setStatus(Status.REMOVED);
            this.getResourceInfoRepository().save(oldResourceInfo);
        }
        return newResourceInfo;
    }

    public void saveResourceInfo(ResourceInfo resourceInfo, StorageType storageType) {
        ResourceInfoRepository repository = this.getResourceInfoRepository();
        Optional<ResourceInfo> resourceInfoOptional = repository
            .findByMediaIdAndStorageTypeAndMediaRefAndStatus(resourceInfo.getMediaId(), storageType,
                resourceInfo.getMediaRef(), Status.ACTIVE);
        resourceInfoOptional.ifPresent(info -> resourceInfo.setId(info.getId()));
        repository.saveAndFlush(resourceInfo);
    }

    public abstract String upload(MultipartFile multipartFile, UploadDTO uploadDTO) throws IOException;

    public abstract void delete(UploadDTO uploadDTO);

    public Resource loadFileAsResource(String fileName, String folder) {
        return null;
    }
}
