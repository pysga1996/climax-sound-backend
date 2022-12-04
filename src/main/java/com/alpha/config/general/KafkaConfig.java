package com.alpha.config.general;

import com.alpha.constant.SchedulerConstants;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @author thanhvt
 * @created 21/08/2021 - 1:50 CH
 * @project vengeance
 * @since 1.0
 **/
@Configuration
@Profile({"heroku", "k8s"})
public class KafkaConfig {

    @Value(value = "${spring.kafka.jaas.options.username:}")
    private String username;

    @Value(value = "${spring.kafka.jaas.options.password:}")
    private String password;

    @Value(value = "${spring.kafka.jaas.options.topic-prefix:}")
    private String topicPrefix;

    private void buildCloudKarafkaProps(Map<String, String> props) {
        String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
        String jaasCfg = String.format(jaasTemplate, username, password);

        String serializer = StringSerializer.class.getName();
        String deserializer = StringDeserializer.class.getName();
//        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880");
//        props.put("bootstrap.servers", brokers);
//        props.put("group.id", "newer");
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", deserializer);
        props.put("value.deserializer", deserializer);
        props.put("key.serializer", serializer);
        props.put("value.serializer", serializer);
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("sasl.jaas.config", jaasCfg);
    }

    @Bean
    @Profile({"heroku"})
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, String> props = kafkaProperties.getProperties();
        this.buildCloudKarafkaProps(props);
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    @Profile({"heroku"})
    public ProducerFactory<Object, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, String> props = kafkaProperties.getProperties();
        this.buildCloudKarafkaProps(props);
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public NewTopic userLikesTopic() {
        return new NewTopic(this.topicPrefix + SchedulerConstants.LIKES_TOPIC, 3, (short) 1);
    }

    @Bean
    public NewTopic userListeningTopic() {
        return new NewTopic(this.topicPrefix + SchedulerConstants.LISTENING_TOPIC, 3, (short) 1);
    }

}
