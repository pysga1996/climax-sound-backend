package com.alpha.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage.firebase")
public class FirebaseStorageProperty {

    private String databaseUrl;

    private String storageBucket;

    private String credentials;
}
