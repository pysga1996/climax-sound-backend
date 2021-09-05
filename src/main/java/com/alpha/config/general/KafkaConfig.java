package com.alpha.config.general;

import com.alpha.service.LikeService.LikeConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * @author thanhvt
 * @created 21/08/2021 - 1:50 CH
 * @project vengeance
 * @since 1.0
 **/
@Configuration
@Profile({"heroku", "poweredge"})
public class KafkaConfig {

    @Value(value = "${spring.kafka.jaas.options.topic-prefix:}")
    private String topicPrefix;

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        kafkaProperties.getProperties().put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880");
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public NewTopic userSongLikeTopic() {
        return new NewTopic(this.topicPrefix + LikeConfig.SONG.getTable(), 3, (short) 1);
    }

    @Bean
    public NewTopic userAlbumLikeTopic() {
        return new NewTopic(this.topicPrefix + LikeConfig.ALBUM.getTable(), 3, (short) 1);
    }

    @Bean
    public NewTopic userArtistLikeTopic() {
        return new NewTopic(this.topicPrefix + LikeConfig.ARTIST.getTable(), 3, (short) 1);
    }
}
