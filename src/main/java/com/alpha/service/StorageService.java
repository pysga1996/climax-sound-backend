package com.alpha.service;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.Status;
import com.alpha.model.dto.ResourceInfoDTO;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
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

    public void deleteResourceInfo(ResourceInfo resourceInfo) {
        this.delete(resourceInfo);
        resourceInfo.setStatus(Status.REMOVED);
//        this.getResourceInfoRepository().delete(resourceInfo);
    }

    public abstract void delete(ResourceInfo resourceInfo);

    public Resource loadFileAsResource(String fileName, String folder) {
        return null;
    }

    public abstract HttpServletRequest getHttpServletRequest();

    public abstract StorageType getStorageType();

    public String getBaseUrl() {
        return this.getStorageType() == StorageType.LOCAL ? this.getHttpServletRequest()
            .getHeader("base-url") : "";
    }

    public String getFullUrl(ResourceInfo resourceInfo) {
        return this.getBaseUrl() + resourceInfo.getUri();
    }

    public String getFullUrl(ResourceInfoDTO resourceInfoDTO) {
        return this.getBaseUrl() + resourceInfoDTO.getUri();
    }

}
