package com.alpha.config.general;

import com.cloudinary.Cloudinary;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Log4j2
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

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "cloudinary")
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }

    @Bean
    @ConditionalOnProperty(prefix = "storage", name = "storage-type", havingValue = "firebase")
    @SneakyThrows
    public StorageClient firebaseStorage() {
        try {
            GoogleCredentials credentials = GoogleCredentials
                .fromStream(new ByteArrayInputStream(this.firebaseCredentials.getBytes()));
//            GoogleCredentials credentials = GoogleCredentials
//                .fromStream(this.firebaseCredFile.getInputStream());
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
            log.error("Could not get admin-sdk json file. Please try again!", ex);
            throw ex;
        }
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
