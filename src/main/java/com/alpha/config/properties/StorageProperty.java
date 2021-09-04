package com.alpha.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage")
public class StorageProperty {

    private StorageType storageType;

    private String temp;

    public enum StorageType {
        LOCAL, CLOUDINARY, FIREBASE, EXTERNAL
    }
}
