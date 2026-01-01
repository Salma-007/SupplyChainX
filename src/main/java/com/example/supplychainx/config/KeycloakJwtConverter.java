package com.example.supplychainx.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakJwtConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String clientId = "SupplyX"; // ⚠️ nom EXACT du client

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> resourceAccess =
                jwt.getClaim("resource_access");

        if (resourceAccess != null &&
                resourceAccess.containsKey(clientId)) {

            Map<String, Object> client =
                    (Map<String, Object>) resourceAccess.get(clientId);

            Collection<String> roles =
                    (Collection<String>) client.get("roles");

            authorities.addAll(
                    roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList())
            );
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}

