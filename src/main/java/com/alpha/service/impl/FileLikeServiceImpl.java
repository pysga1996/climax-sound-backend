package com.alpha.service.impl;

import com.alpha.config.general.KafkaConfig;
import com.alpha.constant.SchedulerConstants.LikeConfig;
import com.alpha.constant.SchedulerConstants.ListeningConfig;
import com.alpha.repositories.LikeRepository;
import com.alpha.service.LikeService;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
public class FileLikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final UserService userService;

    private Map<LikeConfig, FileChannel> likeFileChannelMap;

    private Map<ListeningConfig, FileChannel> listeningFileChannelMap;

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public FileLikeServiceImpl(LikeRepository likeRepository,
        UserService userService,
        RedisTemplate<String, String> redisTemplate) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    @SuppressWarnings({"squid:S2095"})
    private void initQueueFiles() throws IOException {
        Map<LikeConfig, FileChannel> tmpFileChannelMap = new EnumMap<>(LikeConfig.class);
        for (LikeConfig likeConfig : LikeConfig.values()) {
            Path dir = Paths.get(likeConfig.getDir());
            Path filePath = dir.resolve(likeConfig.getLikesQueueFile());
            if (!Files.exists(filePath)) {
                Files.createDirectories(dir).resolve(likeConfig.getLikesQueueFile());
                Files.createFile(filePath);
            }
            try {
                FileChannel fileChannel = FileChannel
                    .open(filePath, StandardOpenOption.APPEND);
                fileChannel.force(true);
                tmpFileChannelMap.put(likeConfig, fileChannel);
            } catch (IOException ex) {
                log.error(ex);
                throw ex;
            }
        }
        this.likeFileChannelMap = Collections.unmodifiableMap(tmpFileChannelMap);
        Map<ListeningConfig, FileChannel> tmpListeningFileChannelMap = new EnumMap<>(
            ListeningConfig.class);
        for (ListeningConfig listeningConfig : ListeningConfig.values()) {
            Path dir = Paths.get(listeningConfig.getDir());
            Path filePath = dir.resolve(listeningConfig.getListeningQueueFile());
            if (!Files.exists(filePath)) {
                Files.createDirectories(dir).resolve(listeningConfig.getListeningQueueFile());
                Files.createFile(filePath);
            }
            try {
                FileChannel fileChannel = FileChannel
                    .open(filePath, StandardOpenOption.APPEND);
                fileChannel.force(true);
                tmpListeningFileChannelMap.put(listeningConfig, fileChannel);
            } catch (IOException ex) {
                log.error(ex);
                throw ex;
            }
        }
        this.listeningFileChannelMap = Collections.unmodifiableMap(tmpListeningFileChannelMap);
    }

    @PreDestroy
    private void closeFiles() {
        this.likeFileChannelMap.values().forEach(fileChannel -> {
            try {
                fileChannel.close();
            } catch (IOException e) {
                log.error(e);
            }
        });
    }

    @Async
    @Override
    public void writeLikesToQueue(String username, Long id, boolean isLiked,
        LikeConfig likeConfig) {
        try (FileLock ignored = this.likeFileChannelMap.get(likeConfig)
            .lock(0, Long.MAX_VALUE, false)) {
            String line = String.format("%s_%d_%b", username, id, isLiked) + System.lineSeparator();
            ByteBuffer buff = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
            this.likeFileChannelMap.get(likeConfig).write(buff);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Async
    @Override
    public void writeListenToQueue(String username, Long id, ListeningConfig listeningConfig) {
        try (FileLock ignored = this.listeningFileChannelMap.get(listeningConfig)
            .lock(0, Long.MAX_VALUE, false)) {
            String line = String.format("%s_%d", username, id) + System.lineSeparator();
            ByteBuffer buff = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
            this.listeningFileChannelMap.get(listeningConfig).write(buff);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    @Transactional
    public void insertLikesToDb(LikeConfig likeConfig) {
        log.debug("Start synchronize likes record to database...");
        Path dir = Paths.get(likeConfig.getDir());
        Path filePath = dir.resolve(likeConfig.getLikesQueueFile());
        try (Stream<String> lines = Files.lines(filePath)) {
            this.likeRepository.updateLikesInBatch(lines.collect(Collectors.toList()), likeConfig);
            try (FileLock ignored = this.likeFileChannelMap.get(likeConfig)
                .lock(0, Long.MAX_VALUE, false)) {
                this.likeFileChannelMap.get(likeConfig).truncate(0);
            } catch (IOException e) {
                log.error(e);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    @Transactional
    public void updateListeningToDb(ListeningConfig listeningConfig) {
        log.debug("Start synchronize listening record to database...");
        Path dir = Paths.get(listeningConfig.getDir());
        Path filePath = dir.resolve(listeningConfig.getListeningQueueFile());
        try (Stream<String> lines = Files.lines(filePath)) {
            this.likeRepository
                .updateListeningInBatch(lines.collect(Collectors.toList()), listeningConfig);
            try (FileLock ignored = this.listeningFileChannelMap.get(listeningConfig)
                .lock(0, Long.MAX_VALUE, false)) {
                this.listeningFileChannelMap.get(listeningConfig).truncate(0);
            } catch (IOException e) {
                log.error(e);
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    @Transactional
    public void updateListeningCountToDb(ListeningConfig listeningConfig) {
        log.debug("Start synchronize listening count to database...");
        Set<String> queues = this.redisTemplate.opsForSet().members("song_listening_queue");
        if (queues != null && !queues.isEmpty()) {
            Map<String, String> idListeningCountMap = queues.stream().collect(Collectors
                .toMap(e -> e, e -> String
                    .valueOf(this.likeRepository.getSongListeningCount(Long.parseLong(e)))));
            this.likeRepository.updateListeningCountInBatch(idListeningCountMap, listeningConfig);
            this.redisTemplate.opsForSet().remove("song_listening_queue", queues.toArray());
        }
    }

    @Override
    public void like(Long id, LikeConfig likeConfig, boolean isLiked) {
        String username = userService.getCurrentUsername();
        // TODO
        this.likeRepository.setUserSongLikeToCache(username, id, isLiked);
        this.writeLikesToQueue(username, id, isLiked, likeConfig);
    }

    @Override
    public void listen(Long id, ListeningConfig listeningConfig, String username) {
        if (username != null) {
            this.writeListenToQueue(username, id, listeningConfig);
        }
        // TODO
        Long listeningCount = this.likeRepository.getSongListeningCount(id);
        this.likeRepository.setSongListeningCountToCache(id, ++listeningCount);
        this.redisTemplate.opsForSet().add("song_listening_queue", String.valueOf(id));
    }
}
