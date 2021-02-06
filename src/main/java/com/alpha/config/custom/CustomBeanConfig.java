package com.alpha.config.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

@Configuration
@SuppressWarnings("deprecation")
public class CustomBeanConfig {

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        return new DefaultUserAuthenticationConverter();
    }
}
