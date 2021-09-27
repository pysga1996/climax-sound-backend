package com.alpha.service.impl;

import com.alpha.config.general.KafkaConfig;
import com.alpha.constant.EntityType;
import com.alpha.constant.SchedulerConstants;
import com.alpha.repositories.FavoritesRepository;
import com.alpha.service.FavoritesService;
import com.alpha.service.UserService;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@ConditionalOnMissingBean({KafkaConfig.class})
@Service
public class FileFavoritesServiceImpl implements FavoritesService {

    @Getter
    private final FavoritesRepository favoritesRepository;

    @Getter
    private final UserService userService;

    private FileChannel likeFileChannel;

    private FileChannel listeningFileChannel;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FileFavoritesServiceImpl(FavoritesRepository favoritesRepository,
        UserService userService,
        RedisTemplate<String, String> redisTemplate) {
        this.favoritesRepository = favoritesRepository;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    @SuppressWarnings({"squid:S2095"})
    private void initQueueFiles() throws IOException {
        Path dir = Paths.get(SchedulerConstants.QUEUE_BASE_DIR);
        Path likeFilePath = dir.resolve(SchedulerConstants.LIKES_QUEUE_FILE);
        if (!Files.exists(likeFilePath)) {
            Files.createDirectories(dir).resolve(SchedulerConstants.LIKES_QUEUE_FILE);
            Files.createFile(likeFilePath);
        }
        try {
            this.likeFileChannel = FileChannel
                .open(likeFilePath, StandardOpenOption.APPEND);
            this.likeFileChannel.force(true);
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
        Path listeningFilePath = dir.resolve(SchedulerConstants.LISTENING_QUEUE_FILE);
        if (!Files.exists(listeningFilePath)) {
            Files.createDirectories(dir).resolve(SchedulerConstants.LISTENING_QUEUE_FILE);
            Files.createFile(listeningFilePath);
        }
        try {
            this.listeningFileChannel = FileChannel
                .open(listeningFilePath, StandardOpenOption.APPEND);
            this.listeningFileChannel.force(true);
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    @PreDestroy
    private void closeFiles() {
        try {
            this.likeFileChannel.close();
            this.listeningFileChannel.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Async
    @Override
    public void writeLikesToQueue(String username, Long id, boolean isLiked,
        EntityType type) {
        try (FileLock ignored = this.likeFileChannel
            .lock(0, Long.MAX_VALUE, false)) {
            String line =
                String.format("%s_%s_%d_%b", type.name(), username, id, isLiked) + System
                    .lineSeparator();
            ByteBuffer buff = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
            this.likeFileChannel.write(buff);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Async
    @Override
    public void writeListenToQueue(String username, Long id, EntityType type) {
        try (FileLock ignored = this.listeningFileChannel
            .lock(0, Long.MAX_VALUE, false)) {
            String line = String.format("%s_%d", username, id) + System.lineSeparator();
            ByteBuffer buff = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
            this.listeningFileChannel.write(buff);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    @Transactional
    public void insertLikesToDb() {
        log.info("Start synchronize likes record to database...");
        Path dir = Paths.get(SchedulerConstants.QUEUE_BASE_DIR);
        Path filePath = dir.resolve(SchedulerConstants.LIKES_QUEUE_FILE);
        try (Stream<String> lines = Files.lines(filePath)) {
            this.favoritesRepository
                .updateLikesInBatch(lines.collect(Collectors.toList()));
            try (FileLock ignored = this.likeFileChannel
                .lock(0, Long.MAX_VALUE, false)) {
                this.likeFileChannel.truncate(0);
            } catch (IOException e) {
                log.error(e);
            }
        } catch (IOException e) {
            log.error(e);
        }
        log.info("End synchronize likes record to database...");
    }

    @Override
    @Transactional
    public void updateListeningToDb() {
        log.info("Start synchronize listening record to database...");
        Path dir = Paths.get(SchedulerConstants.QUEUE_BASE_DIR);
        Path filePath = dir.resolve(SchedulerConstants.LISTENING_QUEUE_FILE);
        try (Stream<String> lines = Files.lines(filePath)) {
            this.favoritesRepository
                .updateListeningInBatch(lines.collect(Collectors.toList()));
            try (FileLock ignored = this.listeningFileChannel
                .lock(0, Long.MAX_VALUE, false)) {
                this.listeningFileChannel.truncate(0);
            } catch (IOException e) {
                log.error(e);
            }
        } catch (IOException e) {
            log.error(e);
        }
        log.info("End synchronize listening record to database...");
    }

    @Override
    @Transactional
    public void updateLikesCountToDb(EntityType type) {
        log.info("Start synchronize likes count to database...");
        String cacheQueue = getLikesCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        Set<String> queues = this.redisTemplate.opsForSet().members(cacheQueue);
        if (queues != null && !queues.isEmpty()) {
            Map<String, String> idLikesCountMap = queues.stream().collect(Collectors
                .toMap(e -> e, e -> String
                    .valueOf(this.favoritesRepository.getLikesCount(Long.parseLong(e), type))));
            this.favoritesRepository
                .updateLikesCountInBatch(idLikesCountMap, type);
            this.redisTemplate.opsForSet().remove(cacheQueue, queues.toArray());
        }
        log.info("End synchronize likes count to database...");
    }

    @Override
    @Transactional
    public void updateListeningCountToDb(EntityType type) {
        log.info("Start synchronize listening count to database...");
        String cacheQueue = getListeningCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        Set<String> queues = this.redisTemplate.opsForSet().members(cacheQueue);
        if (queues != null && !queues.isEmpty()) {
            Map<String, String> idListeningCountMap = queues.stream().collect(Collectors
                .toMap(e -> e, e -> String
                    .valueOf(this.favoritesRepository.getListeningCount(Long.parseLong(e), type))));
            this.favoritesRepository
                .updateListeningCountInBatch(idListeningCountMap, type);
            this.redisTemplate.opsForSet().remove(cacheQueue, queues.toArray());
        }
        log.info("End synchronize listening count to database...");
    }

    @Override
    public void like(Long id, boolean isLiked, EntityType type) {
        String username = userService.getCurrentUsername();
        // TODO
        this.favoritesRepository.setUserLikeToCache(username, id, type, isLiked);
        this.writeLikesToQueue(username, id, isLiked, type);
        Long likesCount = this.favoritesRepository.getLikesCount(id, type);
        this.favoritesRepository.setLikesCountToCache(id, ++likesCount, type);
        String cacheQueue = getLikesCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        this.redisTemplate.opsForSet().add(cacheQueue, String.valueOf(id));
    }

    @Override
    public void listen(Long id, EntityType type) {
        String username = this.userService.getCurrentUsername();
        if (username != null) {
            this.writeListenToQueue(username, id, type);
        }
        // TODO
        Long listeningCount = this.favoritesRepository.getListeningCount(id, type);
        this.favoritesRepository.setListeningCountToCache(id, ++listeningCount, type);
        String cacheQueue = getListeningCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        this.redisTemplate.opsForSet().add(cacheQueue, String.valueOf(id));
    }
}
