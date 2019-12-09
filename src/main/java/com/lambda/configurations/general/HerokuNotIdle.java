package com.lambda.configurations.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HerokuNotIdle {
    @Autowired
    private Environment environment;

    private static final Logger LOG = LoggerFactory.getLogger(HerokuNotIdle.class);

    @Scheduled(fixedDelay=1800000)
    public void herokuNotIdle(){
        LOG.debug("Heroku not idle execution");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(environment.getProperty("BACKEND_HOST") + "/climax-sound/api/test", String.class);
    }
}
