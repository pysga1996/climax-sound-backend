package com.alpha.config.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("deprecation")
public class CustomAccessTokenConverter implements AccessTokenConverter {

    private final UserAuthenticationConverter userTokenConverter;

    private final boolean includeGrantType;

    @Autowired
    public CustomAccessTokenConverter(UserAuthenticationConverter userTokenConverter) {
        this.userTokenConverter = userTokenConverter;
        this.includeGrantType = true;
    }

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        return null;
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        return null;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<>();
        Set<String> scope = extractScope(map);
        Authentication user = userTokenConverter.extractAuthentication(map);
        String clientId = (String) map.get(CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);
        if (includeGrantType && map.containsKey(GRANT_TYPE)) {
            parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
        }
        Set<String> resourceIds = new LinkedHashSet<>(map.containsKey(AUD) ?
                getAudience(map) : Collections.emptySet());

        Collection<? extends GrantedAuthority> authorities = null;
        if (user == null && map.containsKey(AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(roles);
        }
        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities,
                true, scope, resourceIds, null, null, null);
        return new OAuth2Authentication(request, user);
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        String scopeAttribute = OAuth2AccessToken.SCOPE;
        if (map.containsKey(scopeAttribute)) {
            Object scopeObj = map.get(scopeAttribute);
            if (scopeObj instanceof String) {
                scope = new LinkedHashSet<>(Arrays.asList(((String) scopeObj).split(" ")));
            } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
                @SuppressWarnings("unchecked")
                Collection<String> scopeColl = (Collection<String>) scopeObj;
                scope = new LinkedHashSet<>(scopeColl);    // Preserve ordering
            }
        }
        return scope;
    }

    private Collection<String> getAudience(Map<String, ?> map) {
        Object audiences = map.get(AUD);
        if (audiences instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<String> result = (Collection<String>) audiences;
            return result;
        }
        return Collections.singleton((String) audiences);
    }
}
