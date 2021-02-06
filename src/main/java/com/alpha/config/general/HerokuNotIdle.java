package com.alpha.config.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

//@Component
public class HerokuNotIdle {
    private static final Logger LOG = LoggerFactory.getLogger(HerokuNotIdle.class);
    private final Environment environment;

    //    @Autowired
    public HerokuNotIdle(Environment environment) {
        this.environment = environment;
    }

    @Scheduled(fixedDelay = 120000)
    public void herokuNotIdle() {
        LOG.debug("Heroku not idle execution");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(environment.getProperty("BACKEND_HOST") + "/alpha-sound/api/test", String.class);
    }
}
