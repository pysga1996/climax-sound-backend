package com.alpha;

import com.alpha.event.DataSeedingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.alpha"})
@EntityScan(basePackages = {"com.alpha.model"})
@EnableJpaRepositories(basePackages = {"com.alpha.repositories"})
public class AlphaSoundServiceApplication extends SpringBootServletInitializer {

    private final DataSeedingListener dataSeedingListener;

    @Autowired
    public AlphaSoundServiceApplication(DataSeedingListener dataSeedingListener) {
        this.dataSeedingListener = dataSeedingListener;
    }

    public static void main(String[] args) {
        SpringApplication.run(AlphaSoundServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder) {
        return applicationBuilder.sources(AlphaSoundServiceApplication.class);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void dataSeeding() {
        dataSeedingListener.onApplicationEvent();
    }

}
