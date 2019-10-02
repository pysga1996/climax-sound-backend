package com.lambda.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService<T> {
    String storeFile(MultipartFile file, T t);
    Resource loadFileAsResource(String fileName);
    Boolean deleteFile(String fileName);
}
