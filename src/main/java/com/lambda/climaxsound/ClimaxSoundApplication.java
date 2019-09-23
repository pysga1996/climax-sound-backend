package com.lambda.climaxsound;

import com.lambda.configuration.security.DataSeedingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ComponentScan("com.lambda")
@EntityScan( basePackages = {"com.lambda.model"} )
public class ClimaxSoundApplication {

	@Autowired
	DataSeedingListener dataSeedingListener;

	public static void main(String[] args) {

		SpringApplication.run(ClimaxSoundApplication.class, args);

	}

	@EventListener(ContextRefreshedEvent.class)
	public void dataSeeding() {
		dataSeedingListener.onApplicationEvent();
	}



}
