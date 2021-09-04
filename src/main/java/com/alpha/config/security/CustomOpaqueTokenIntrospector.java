package com.alpha.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Component
@Primary
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private static final String USERNAME = "user_name";

    private static final String AUTHORITIES = "authorities";

    private static final String ISSUE_AT = "iat";

    private static final String EXPIRATION = "exp";
    private final RestOperations restOperations;
    private final ObjectMapper objectMapper;
    @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
    private String introspectionUri;

    @Autowired
    public CustomOpaqueTokenIntrospector(
        RestOperations restOperations, ObjectMapper objectMapper) {
        this.restOperations = restOperations;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("rawtypes")
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            ResponseEntity<Map> responseEntity = this.makeRequest(token);
            if (responseEntity.getBody() == null || !(responseEntity.getBody().get("active")
                == Boolean.TRUE)) {
                log.debug("Did not validate token since it is inactive");
                throw new BadOpaqueTokenException("Provided token isn't active");
            } else {
                String jsonString = this.objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseEntity.getBody());
                log.debug("Check Token Response: \n{}", jsonString);
                return this.convertClaimsSet(responseEntity.getBody());
            }
        } catch (JsonProcessingException ex) {
            log.error("Error while introspecting token", ex);
            throw new OAuth2IntrospectionException(ex.getMessage(), ex);
        } catch (HttpClientErrorException ex) {
            log.error("Error while introspecting token", ex);
            throw new OAuth2IntrospectionException(ex.getResponseBodyAsString(), ex);
        }
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<Map> makeRequest(String token) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.introspectionUri)
            .queryParam("token", token);
        return this.restOperations.postForEntity(builder.toUriString(), new HashMap<>(), Map.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private OAuth2AuthenticatedPrincipal convertClaimsSet(Map response) {
        Collection<GrantedAuthority> authorities = ((List<String>) response.get(AUTHORITIES))
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        Instant iat = Instant.ofEpochSecond((int) response.get(ISSUE_AT));
        Instant exp = Instant.ofEpochSecond((int) response.get(EXPIRATION));
        response.replace(ISSUE_AT, iat);
        response.replace(EXPIRATION, exp);
        return new OAuth2IntrospectionAuthenticatedPrincipal((String) response.get(USERNAME),
            response, authorities);
    }

}
