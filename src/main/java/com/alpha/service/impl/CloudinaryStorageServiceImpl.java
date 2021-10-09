package com.alpha.service.impl;

import com.alpha.config.properties.StorageProperty.StorageType;
import com.alpha.constant.ModelStatus;
import com.alpha.model.entity.Media;
import com.alpha.model.entity.ResourceInfo;
import com.alpha.repositories.ResourceInfoRepository;
import com.alpha.service.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Service
@DependsOn("cloudinary")
@ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "cloudinary")
public class CloudinaryStorageServiceImpl extends StorageService {

    private final Cloudinary cloudinary;

    private final ServletContext servletContext;

    private final ResourceInfoRepository resourceInfoRepository;
    @Value("${storage.temp}")
    private String tempFolder;

    @Getter
    @Setter
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    public CloudinaryStorageServiceImpl(Cloudinary cloudinary, ServletContext servletContext,
        ResourceInfoRepository resourceInfoRepository) {
        this.cloudinary = cloudinary;
        this.servletContext = servletContext;
        this.resourceInfoRepository = resourceInfoRepository;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.CLOUDINARY;
    }

    @Override
    @Transactional
    @SneakyThrows
    public ResourceInfo upload(MultipartFile multipartFile, Media media) {
        ResourceInfo resourceInfo = media.generateResource(multipartFile);
        try {
            this.deleteOldResources(resourceInfo, StorageType.CLOUDINARY);
            File tmpDir = new File(servletContext.getRealPath("/") + this.tempFolder);
            if (!tmpDir.exists()) {
                log.info("Created temp folder? {}", tmpDir.mkdir());
            }
            File tmpFile = new File(tmpDir, resourceInfo.getFileName());
            if (!tmpFile.exists()) {
                log.info("Created temp file? {}", tmpFile.createNewFile());
            }
            log.info("Path: {}", tmpFile.getCanonicalPath());
            multipartFile.transferTo(tmpFile);
            JSONArray accessControl = new JSONArray();
            JSONObject accessType = new JSONObject();
            accessType.put("access_type", "anonymous");
            accessControl.put(accessType);
            Map<?, ?> params = ObjectUtils.asMap(
                "use_filename", true,
                "folder", resourceInfo.getFolder(),
                "unique_filename", false,
                "overwrite", true,
                "resource_type", "auto",
                "access_control", accessControl
            );
            Map<?, ?> uploadResult = this.cloudinary.uploader().upload(tmpFile, params);
            log.info("Delete temp file? {}", tmpFile.delete());
            String publicId = (String) uploadResult.get("public_id");
            resourceInfo.setStoragePath(publicId);
            String mediaUrl = (String) uploadResult.get("secure_url");
            resourceInfo.setUri(mediaUrl);
            resourceInfo.setStorageType(StorageType.CLOUDINARY);
            resourceInfo.setStatus(ModelStatus.ACTIVE);
            this.saveResourceInfo(resourceInfo, StorageType.CLOUDINARY);
            return resourceInfo;
        } catch (IOException ex) {
            log.error("Could not store file {}. Please try again!",
                resourceInfo.getFileName(), ex);
            throw ex;
        }
    }

    @Override
    public void delete(ResourceInfo resourceInfo) {
        Map<String, Object> deleteOption = new HashMap<>();
        deleteOption.put("invalidate", true);
        Map<?, ?> deleteResult;
        try {
            deleteResult = this.cloudinary.uploader()
                .destroy(resourceInfo.getStoragePath(), deleteOption);
            if (deleteResult.get("result").equals("ok")) {
                log.info("Delete resource success {}", resourceInfo.getStoragePath());
            } else {
                log.error("Delete resource failed {}", resourceInfo.getStoragePath());
            }
        } catch (IOException ex) {
            log.error("Delete resource failed {} {}", resourceInfo.getStoragePath(), ex);
        }
    }

    @Override
    public ResourceInfoRepository getResourceInfoRepository() {
        return resourceInfoRepository;
    }
}
