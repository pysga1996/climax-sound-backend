package com.alpha.config.general;

import com.alpha.error.FileStorageException;
import com.cloudinary.Cloudinary;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Configuration
public class CommonConfig {

    @Value("${storage.cloudinary.url}")
    private String cloudinaryUrl;

    @Value("${storage.firebase.database-url}")
    private String firebaseDatabaseUrl;

    @Value("${storage.firebase.storage-bucket}")
    private String firebaseStorageBucket;

    @Value("${storage.firebase.credentials}")
    private String firebaseCredentials;

    @Value("${custom.http-port}")
    private Integer httpPort;

    @Value("${custom.https-port}")
    private Integer httpsPort;

    @Value("${custom.security-policy}")
    private String securityPolicy;

    @Value("${custom.connector-scheme}")
    private String connectorScheme;

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "cloudinary")
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "firebase")
    public StorageClient firebaseStorage() {
        try {
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(this.firebaseCredentials.getBytes()));
            GoogleCredentials credentials = GoogleCredentials
                .fromStream(this.firebaseCredFile.getInputStream());
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setDatabaseUrl(this.firebaseDatabaseUrl)
                .setStorageBucket(this.firebaseStorageBucket)
                .build();

            FirebaseApp fireApp = null;
            List<FirebaseApp> firebaseApps = FirebaseApp.getApps();
            if (firebaseApps != null && !firebaseApps.isEmpty()) {
                for (FirebaseApp app : firebaseApps) {
                    if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                        fireApp = app;
                    }
                }
            } else {
                fireApp = FirebaseApp.initializeApp(options);
            }
            return StorageClient.getInstance(Objects.requireNonNull(fireApp));
        } catch (IOException ex) {
            throw new FileStorageException("Could not get admin-sdk json file. Please try again!", ex);
        }
    }

    @Bean
    @ConditionalOnCloudPlatform(CloudPlatform.NONE)
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                // set to CONFIDENTIAL to automatically redirect from http to https port
                securityConstraint.setUserConstraint(securityPolicy);
//                securityConstraint.setUserConstraint("NONE");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(getHttpConnector());
        return tomcat;
    }

    private Connector getHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme(connectorScheme);
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);
        connector.addUpgradeProtocol(new Http2Protocol());
        return connector;
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ZERO)
            .disableCachingNullValues()
            .serializeValuesWith(
                SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
            .withCacheConfiguration("songLikeCache",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ZERO));
    }

    @Bean(name = "threadPoolTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}
