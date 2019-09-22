package com.lambda.climaxsound;

import com.lambda.configuration.general.ApplicationConfiguration;
import com.lambda.configuration.general.ApplicationInitializer;
import com.lambda.configuration.security.DataSeedingListener;
import com.lambda.configuration.security.SecurityConfiguration;
import com.lambda.configuration.security.SecurityWebApplicationInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;


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
