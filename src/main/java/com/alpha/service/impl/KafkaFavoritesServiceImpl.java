package com.alpha.service.impl;

import com.alpha.config.general.KafkaConfig;
import com.alpha.constant.EntityType;
import com.alpha.constant.SchedulerConstants;
import com.alpha.repositories.FavoritesRepository;
import com.alpha.service.FavoritesService;
import com.alpha.service.UserService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author thanhvt
 * @created 21/08/2021 - 1:57 CH
 * @project vengeance
 * @since 1.0
 **/
@Log4j2
@Service
@ConditionalOnBean({KafkaConfig.class})
public class KafkaFavoritesServiceImpl implements FavoritesService {

    @Getter
    private final FavoritesRepository favoritesRepository;

    @Getter
    private final UserService userService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Consumer<String, String> consumer;

    private final RedisTemplate<String, String> redisTemplate;

    @Value(value = "${spring.kafka.jaas.options.topic-prefix:}")
    private String topicPrefix;

    @Autowired
    public KafkaFavoritesServiceImpl(FavoritesRepository favoritesRepository,
        UserService userService,
        KafkaTemplate<String, String> kafkaTemplate,
        ConsumerFactory<String, String> consumerFactory,
        RedisTemplate<String, String> redisTemplate) {
        this.favoritesRepository = favoritesRepository;
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
        boolean isLiked, EntityType type) {
        String record = String.format("%s_%s_%d_%b", type.name(), username, id, isLiked);
        String topic = this.topicPrefix + SchedulerConstants.LIKES_TOPIC;
        log.info("Send like to topic {}: {}", topic, record);
        this.kafkaTemplate.send(topic, record);
    }

    @Override
    public void writeListenToQueue(String username, Long id, EntityType type) {
        String record = String.format("%s_%s_%d", type.name(), username, id);
        String topic = this.topicPrefix + SchedulerConstants.LISTENING_TOPIC;
        log.info("Send listening to topic {}: {}", topic, record);
        this.kafkaTemplate.send(topic, record);
    }

    @Override
    public void insertLikesToDb() {
        try {
            String topic = this.topicPrefix + SchedulerConstants.LIKES_TOPIC;
            log.info("Start insert likes from topic {} to database...", topic);
            this.consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, String> records = this.consumer
                .poll(Duration.of(10, ChronoUnit.SECONDS));
            List<String> buffer = new ArrayList<>();
//            Map<TopicPartition, OffsetAndMetadata> mapTopicOffset = new HashMap<>();
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record.value());
//                mapTopicOffset.put(new TopicPartition(record.topic(), record.partition()),
//                        new OffsetAndMetadata(record.offset(), record.leaderEpoch(), record.value()));
            }
            if (!buffer.isEmpty()) {
                this.favoritesRepository.updateLikesInBatch(buffer);
                this.consumer.commitSync();
                buffer.clear();
            }
        } catch (RuntimeException e) {
            log.error(e);
        } finally {
            this.consumer.unsubscribe();
        }
    }

    @Override
    public void updateListeningToDb() {
        try {
            String topic = this.topicPrefix + SchedulerConstants.LISTENING_TOPIC;
            log.info("Start insert listening from topic {} to database...", topic);
            this.consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, String> records = this.consumer
                .poll(Duration.of(10, ChronoUnit.SECONDS));
            List<String> buffer = new ArrayList<>();
            for (ConsumerRecord<String, String> record : records) {
                buffer.add(record.value());
            }
            if (!buffer.isEmpty()) {
                this.favoritesRepository.updateListeningInBatch(buffer);
                this.consumer.commitSync();
                buffer.clear();
            }
        } catch (RuntimeException e) {
            log.error(e);
        } finally {
            this.consumer.unsubscribe();
        }
    }

    @Override
    @Transactional
    public void updateLikesCountToDb(EntityType type) {
        log.debug("Start synchronize likes count to database...");
        String cacheQueue = getLikesCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        Set<String> queues = this.redisTemplate.opsForSet().members(cacheQueue);
        if (queues != null && !queues.isEmpty()) {
            Map<String, String> idLikesCountMap = queues.stream().collect(Collectors
                .toMap(e -> e, e -> String
                    .valueOf(this.favoritesRepository.getListeningCount(Long.parseLong(e), type))));
            this.favoritesRepository
                .updateLikesCountInBatch(idLikesCountMap, type);
            this.redisTemplate.opsForSet().remove(cacheQueue, queues.toArray());
        }
    }

    @Override
    public void updateListeningCountToDb(EntityType type) {
        log.info("Start synchronize listening count to database...");
        Set<String> queues = this.redisTemplate.opsForSet()
            .members(SchedulerConstants.LISTENING_CACHE);
        if (queues != null && !queues.isEmpty()) {
            Map<String, String> idListeningCountMap = queues.stream().collect(Collectors
                .toMap(e -> e, e -> String
                    .valueOf(this.favoritesRepository.getListeningCount(Long.parseLong(e), type))));
            this.favoritesRepository
                .updateListeningCountInBatch(idListeningCountMap, type);
            this.redisTemplate.opsForSet()
                .remove(SchedulerConstants.LISTENING_CACHE, queues.toArray());
        }
        log.info("End synchronize listening count to database...");
    }

    @Override
    public void like(Long id, boolean isLiked, EntityType type) {
        String username = userService.getCurrentUsername();
        this.favoritesRepository.setUserLikeToCache(username, id, type, isLiked);
        this.writeLikesToQueue(username, id, isLiked, type);
        String cacheQueue = getLikesCacheQueue(type);
        if (cacheQueue == null) {
            return;
        }
        this.redisTemplate.opsForSet().add(cacheQueue, String.valueOf(id));
    }

    @Override
    @Transactional
    public void listen(Long id, EntityType type) {
        if (this.userService.isAuthenticated()) {
            this.writeListenToQueue(this.userService.getCurrentUsername(), id, type);
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
