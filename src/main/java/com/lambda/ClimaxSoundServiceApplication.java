package com.lambda;

import com.lambda.event.listener.DataSeedingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.lambda"})
@EntityScan(basePackages = {"com.lambda.models"})
@EnableJpaRepositories(basePackages = {"com.lambda.repositories"})
@EnableScheduling
public class ClimaxSoundServiceApplication extends SpringBootServletInitializer {

    private final DataSeedingListener dataSeedingListener;

    @Autowired
    public ClimaxSoundServiceApplication(DataSeedingListener dataSeedingListener) {
        this.dataSeedingListener = dataSeedingListener;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        return applicationBuilder.sources(ClimaxSoundServiceApplication.class);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void dataSeeding() {
        dataSeedingListener.onApplicationEvent();
    }

    public static void main(String[] args) {
        SpringApplication.run(ClimaxSoundServiceApplication.class, args);
    }

}
