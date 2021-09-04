package com.alpha.config.general;

import com.alpha.service.LikeService.LikeConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
