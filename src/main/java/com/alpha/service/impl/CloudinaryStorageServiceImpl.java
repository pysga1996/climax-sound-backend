package com.alpha.service.impl;

import com.alpha.model.util.UploadObject;
import com.alpha.service.StorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.log4j.Log4j2;
import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Log4j2
@Service
@Profile({"heroku"})
public class CloudinaryStorageServiceImpl extends StorageService {

    @Value("${storage.temp}")
    private String tempFolder;

    private final Cloudinary cloudinary;

    private final ServletContext servletContext;

    @Autowired
    public CloudinaryStorageServiceImpl(Cloudinary cloudinary, ServletContext servletContext) {
        this.cloudinary = cloudinary;
        this.servletContext = servletContext;
    }

    @Override
    public String upload(MultipartFile multipartFile, UploadObject uploadObject) throws IOException {
        File tmpDir = new File(servletContext.getRealPath("/") + this.tempFolder);
        if (!tmpDir.exists()) {
            log.info("Created temp folder? {}", tmpDir.mkdir());
        }
        String ext = this.getExtension(multipartFile);
        String filename = uploadObject.createFileName(ext);
        File tmpFile = new File(tmpDir, filename);
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
                "folder", uploadObject.getFolder(),
                "unique_filename", false,
                "overwrite", true,
                "resource_type", "image",
                "access_control", accessControl
        );
        Map<?, ?> uploadResult = this.cloudinary.uploader().upload(tmpFile, params);
        log.info("Delete temp file? {}", tmpFile.delete());
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void delete(UploadObject uploadObject) {

    }
}
