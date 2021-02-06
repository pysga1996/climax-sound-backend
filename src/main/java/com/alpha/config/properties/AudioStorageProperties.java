package com.alpha.config.properties;

import org.springframework.stereotype.Component;

@Component
//@ConfigurationProperties(prefix = "audio")
public class AudioStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
