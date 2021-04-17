package com.alpha.config.custom;

import com.alpha.model.dto.CheckTokenResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
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
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Component
@Primary
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    @Value("${spring.security.oauth2.resourceserver.opaquetoken.introspection-uri}")
    private String introspectionUri;

    private final RestOperations restOperations;

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomOpaqueTokenIntrospector(
        RestOperations restOperations, ObjectMapper objectMapper) {
        this.restOperations = restOperations;
        this.objectMapper = objectMapper;
    }

    public OAuth2AuthenticatedPrincipal introspect(String token) {
        ResponseEntity<CheckTokenResponseDTO> responseEntity = this.makeRequest(token);
        if (responseEntity.getBody() == null || !responseEntity.getBody().getActive()) {
            log.trace("Did not validate token since it is inactive");
            throw new BadOpaqueTokenException("Provided token isn't active");
        } else {
            String jsonString = null;
            try {
                jsonString = this.objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(responseEntity.getBody());
            } catch (JsonProcessingException e) {
                log.error("Error print json response", e);
            }
            log.info("Check Token Response: \n{}", jsonString);
            return this.convertClaimsSet(responseEntity.getBody());
        }
    }

    private ResponseEntity<CheckTokenResponseDTO> makeRequest(String token) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.introspectionUri)
                    .queryParam("token", token);
            return this.restOperations.postForEntity(builder.toUriString(), null, CheckTokenResponseDTO.class);
        } catch (Exception var3) {
            throw new OAuth2IntrospectionException(var3.getMessage(), var3);
        }
    }

    @SuppressWarnings("unchecked")
    private OAuth2AuthenticatedPrincipal convertClaimsSet(CheckTokenResponseDTO response) {
        Collection<GrantedAuthority> authorities = Arrays.stream(response.getAuthorities())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        Map<String, Object> attributes = this.objectMapper.convertValue(response, Map.class);
        attributes.replace("exp", Instant.ofEpochMilli(response.getExp()));
        return new OAuth2IntrospectionAuthenticatedPrincipal(response.getUserName(), attributes, authorities);
    }

}
