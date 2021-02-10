package com.alpha.config.general;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

@Log4j2
//@Component
public class HerokuNotIdle {

    private final Environment environment;

    //    @Autowired
    public HerokuNotIdle(Environment environment) {
        this.environment = environment;
    }

    @Scheduled(fixedDelay = 120000)
    public void herokuNotIdle() {
        log.debug("Heroku not idle execution");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject(environment.getProperty("BACKEND_HOST") + "/alpha-sound/api/test", String.class);
    }
}
