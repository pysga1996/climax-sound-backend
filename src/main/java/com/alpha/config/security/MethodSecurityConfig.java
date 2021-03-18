package com.alpha.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, order = Ordered.HIGHEST_PRECEDENCE)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final MethodSecurityExpressionHandler methodSecurityExpressionHandler;

    @Autowired
    public MethodSecurityConfig(MethodSecurityExpressionHandler methodSecurityExpressionHandler) {
        this.methodSecurityExpressionHandler = methodSecurityExpressionHandler;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setDefaultRolePrefix("");
        return this.methodSecurityExpressionHandler;
    }
}
