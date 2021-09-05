package com.alpha.service.impl;

import com.alpha.config.general.KafkaConfig;
import com.alpha.repositories.LikeRepository;
import com.alpha.service.LikeService;
import com.alpha.service.UserService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author thanhvt
 * @created 21/08/2021 - 1:57 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Service
@ConditionalOnBean({KafkaConfig.class})
public class KafkaLikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final UserService userService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Consumer<String, String> consumer;

    private final RedisTemplate<String, String> redisTemplate;

    @Value(value = "${spring.kafka.jaas.options.topic-prefix:}")
    private String topicPrefix;

    @Autowired
    public KafkaLikeServiceImpl(LikeRepository likeRepository,
        UserService userService,
        KafkaTemplate<String, String> kafkaTemplate,
        ConsumerFactory<String, String> consumerFactory,
        RedisTemplate<String, String> redisTemplate) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.consumer = consumerFactory.createConsumer();
        this.redisTemplate = redisTemplate;
//        this.consumer.subscribe(Arrays.stream(LikeConfig.values())
//            .map(LikeConfig::getTable).collect(
//                Collectors.toList()));
//        ConsumerRecords<String, String> records = this.consumer
//            .poll(Duration.of(10, ChronoUnit.SECONDS));
//        for (TopicPartition partition : records.partitions()) {
//            List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
//            long firstOffset = partitionRecords.get(0).offset();
//            consumer.commitSync(Collections.singletonMap(partition,
//                new OffsetAndMetadata(firstOffset > 0 ? firstOffset - 1 : 0)));
//        }
//        this.consumer.unsubscribe();
    }

    @Async
    @Override
    public void writeLikesToQueue(String username, Long id,
        boolean isLiked, LikeConfig likeConfig) {
        String record = String.format("%s_%d_%b", username, id, isLiked);
        this.kafkaTemplate.send(this.topicPrefix + likeConfig.getTable(), record);
    }

    @Override
    public void writeListenToQueue(String username, Long id, ListeningConfig listeningConfig) {
        String record = String.format("%s_%d", username, id);
        this.kafkaTemplate.send(this.topicPrefix + listeningConfig.getTable(), record);
    }

    @Override
    public void insertLikesToDb(LikeConfig likeConfig,
        int batchSize) {
        try {
            log.debug("Start insert {} to database...", likeConfig.getTable());
            this.consumer.subscribe(Collections.singletonList(this.topicPrefix + likeConfig.getTable()));
            ConsumerRecords<String, String> records = this.consumer
                .poll(Duration.of(10, ChronoUnit.SECONDS));
            List<String> buffer = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record.value());
            }
            if (!buffer.isEmpty()) {
                this.likeRepository.updateLikesInBatch(buffer, likeConfig);
                this.consumer.commitSync();
                buffer.clear();
            }
        } catch (RuntimeException e) {
            log.error(e);
        } finally {
            this.consumer.unsubscribe();
        }
    }

    @Scheduled(fixedDelay = 300000) // 5 min
    @Override
    public void insertSongLikesToDb() {
        this.insertLikesToDb(LikeConfig.SONG, 20);
    }

//    @Scheduled(fixedDelay = 300000)
    @Override
    public void insertAlbumLikesToDb() {
        this.insertLikesToDb(LikeConfig.ALBUM, 20);
    }

//    @Scheduled(fixedDelay = 300000)
    @Override
    public void insertArtistLikesToDb() {
        this.insertLikesToDb(LikeConfig.ARTIST, 20);
    }

    @Override
    public void updateListeningToDb(ListeningConfig listeningConfig, int batchSize) {
        try {
            log.debug("Start insert {} to database...", listeningConfig.getTable());
            this.consumer.subscribe(Collections.singletonList(this.topicPrefix + listeningConfig.getTable()));
            ConsumerRecords<String, String> records = this.consumer
                .poll(Duration.of(10, ChronoUnit.SECONDS));
            List<String> buffer = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record.value());
            }
            if (!buffer.isEmpty()) {
                this.likeRepository.updateListeningInBatch(buffer, listeningConfig);
                this.consumer.commitSync();
                buffer.clear();
            }
        } catch (RuntimeException e) {
            log.error(e);
        } finally {
            this.consumer.unsubscribe();
        }
    }

//    @Scheduled(fixedDelay = 300000)
    @Override
    public void updateSongListeningToDb() {
        this.updateListeningToDb(ListeningConfig.SONG, 20);
    }

//    @Scheduled(fixedDelay = 300000)
    @Override
    public void updateAlbumListeningToDb() {
        this.updateListeningToDb(ListeningConfig.ALBUM, 20);
    }

    @Override
    public void updateListeningCountToDb(ListeningConfig listeningConfig, int batchSize) {
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

//    @Scheduled(fixedDelay = 300000) // 5 min
    @Override
    public void updateSongListeningCountToDb() {
        this.updateListeningCountToDb(ListeningConfig.SONG, 20);
    }

//    @Scheduled(fixedDelay = 300000)
    @Override
    public void updateAlbumListeningCountToDb() {
        this.updateListeningCountToDb(ListeningConfig.ALBUM, 20);
    }

    @Override
    public void like(Long id, LikeConfig likeConfig, boolean isLiked) {
        String username = userService.getCurrentUsername();
        this.likeRepository.setUserSongLikeToCache(username, id, isLiked);
        this.writeLikesToQueue(username, id, isLiked, likeConfig);
    }

    @Override
    public void listen(Long id, ListeningConfig listeningConfig, String username) {

    }
}
