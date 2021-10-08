package com.alpha.service;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.EntityStatus;
import com.alpha.elastic.model.ResourceMapEs;
import com.alpha.model.dto.ResourceInfoDTO;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public abstract class StorageService {

    protected abstract ResourceInfoRepository getResourceInfoRepository();

    public abstract ResourceInfo upload(MultipartFile multipartFile, Media media);

    public void deleteOldResources(ResourceInfo resourceInfo, StorageType storageType) {
        List<ResourceInfo> resourceInfoList = this.getResourceInfoRepository()
            .findAllByMediaIdAndStorageTypeAndMediaRefAndStatus(resourceInfo.getMediaId(),
                storageType,
                resourceInfo.getMediaRef(), EntityStatus.ACTIVE);
        resourceInfoList.forEach(this::deleteResourceInfo);
    }

    public void saveResourceInfo(ResourceInfo resourceInfo, StorageType storageType) {
        this.getResourceInfoRepository().saveAndFlush(resourceInfo);
    }

    public void deleteResourceInfo(ResourceInfo resourceInfo) {
        this.delete(resourceInfo);
        resourceInfo.setStatus(EntityStatus.REMOVED);
        this.getResourceInfoRepository().save(resourceInfo);
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

    public String getFullUrl(ResourceMapEs resourceMapEs) {
        if (resourceMapEs == null) return "";
        String url = "";
        switch (this.getStorageType()) {
            case FIREBASE:
                url = resourceMapEs.getFirebaseUrl();
                break;
            case CLOUDINARY:
                url = resourceMapEs.getCloudinaryUrl();
                break;
            case LOCAL:
                url = resourceMapEs.getLocalUri();
                break;
        }
        return this.getBaseUrl() + url;
    }

}
