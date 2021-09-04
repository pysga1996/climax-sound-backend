package com.alpha.config.security;

import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoderJwkSupport;
import org.springframework.security.oauth2.provider.vote.ScopeVoter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.web.client.RestOperations;

@Configuration
@EnableWebSecurity
@SuppressWarnings("deprecation")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;
    private final Environment env;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final RestOperations restOperations;
    private final BearerTokenResolver bearerTokenResolver;
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Autowired
    public SecurityConfig(
        OpaqueTokenIntrospector opaqueTokenIntrospector,
        Environment env,
        AccessDeniedHandler accessDeniedHandler,
        AuthenticationEntryPoint authenticationEntryPoint,
        RestOperations restOperations,
        BearerTokenResolver bearerTokenResolver) {
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
        this.env = env;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.restOperations = restOperations;
        this.bearerTokenResolver = bearerTokenResolver;
    }

    @Bean
    @Primary
    public JwtDecoder customJwtDecoder() {
        NimbusJwtDecoderJwkSupport nimbusJwtDecoderJwkSupport = new NimbusJwtDecoderJwkSupport(
            this.jwkSetUri);
        nimbusJwtDecoderJwkSupport.setRestOperations(this.restOperations);
        return nimbusJwtDecoderJwkSupport;
    }

    @Bean
    @Primary
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter jwtCollectionConverter = new JwtGrantedAuthoritiesConverter();
        jwtCollectionConverter.setAuthorityPrefix("");
        jwtCollectionConverter.setAuthoritiesClaimName("authorities");
        return jwtCollectionConverter;
    }

    @Bean
    @Primary
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationTokenConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter
            .setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        java.util.List<AccessDecisionVoter<? extends Object>> decisionVoters
            = Arrays.asList(
            new WebExpressionVoter(),
            // You can add or remove the Role voters as per need
            new RoleVoter(),                                 // For ROLE_ prefix
            new AuthenticatedVoter(),
            scopeVoterWithNoPrefix()                          // Get instance of ScopeVoter
        );
        return new UnanimousBased(decisionVoters);
    }

    @Bean
    public ScopeVoter scopeVoterWithNoPrefix() {
        ScopeVoter scopeVoter = new ScopeVoter();
        scopeVoter.setScopePrefix("");
        return scopeVoter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
            // Heroku https config
            .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
            .requiresSecure().and()
            .authorizeRequests()
            .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
            .antMatchers("/oauth/token", "/api/login", "/api/register", "/api/song/download/**",
                "/api/song/upload", "/api/album/upload", "/api/album/download/**", "/api/**")
            .permitAll()
            .and()
            .csrf().disable()
            .cors()
            .and()
            .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {
                if (CloudPlatform.HEROKU.isActive(this.env) || Arrays
                    .asList(this.env.getActiveProfiles()).contains("poweredge")) {
                    httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> {
                        jwtConfigurer.decoder(customJwtDecoder());
                        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationTokenConverter());
                    });
                } else {
                    httpSecurityOAuth2ResourceServerConfigurer.opaqueToken(opaqueTokenConfigurer ->
                        opaqueTokenConfigurer.introspector(opaqueTokenIntrospector));
                }
                httpSecurityOAuth2ResourceServerConfigurer
                    .bearerTokenResolver(this.bearerTokenResolver)
                    .accessDeniedHandler(this.accessDeniedHandler)
                    .authenticationEntryPoint(this.authenticationEntryPoint);
            })
            .headers()
            .frameOptions().sameOrigin().disable()
            .authorizeRequests().accessDecisionManager(accessDecisionManager())
            .anyRequest()
            .permitAll().and();
    }
}
