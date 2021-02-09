package com.alpha.service;

import com.alpha.error.FileStorageException;
import com.alpha.model.util.UploadObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public abstract class StorageService {

    protected void normalizeFileName(String fileName) {
        if (fileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
    }

    protected String getExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName != null ?
                originalFileName.substring(originalFileName.lastIndexOf(".") + 1) : "";
    }

    public abstract String upload(MultipartFile multipartFile, UploadObject uploadObject) throws IOException;

    public abstract void delete(UploadObject uploadObject);

    public Resource loadFileAsResource(String fileName, String folder) {
        return null;
    }
}
