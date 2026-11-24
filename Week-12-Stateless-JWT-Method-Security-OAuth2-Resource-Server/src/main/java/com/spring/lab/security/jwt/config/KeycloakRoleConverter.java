package com.spring.lab.security.jwt.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt source) {
        if (source == null || source.getClaims() == null) {
            return Collections.emptyList();
        }

        Set<GrantedAuthority> authorities = new HashSet<>();

        // 1. Extract Realm Access Roles: realm_access.roles
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");
            if (realmRoles != null) {
                realmRoles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
        }

        // 2. Extract Client Scoped Roles: resource_access.<client>.roles
        Map<String, Object> resourceAccess = (Map<String, Object>) source.getClaims().get("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> clientEntry : resourceAccess.entrySet()) {
                if (clientEntry.getValue() instanceof Map<?, ?> clientMap && clientMap.containsKey("roles")) {
                    List<String> clientRoles = (List<String>) clientMap.get("roles");
                    if (clientRoles != null) {
                        clientRoles.stream()
                                .filter(role -> role != null && !role.isBlank())
                                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase())
                                .map(SimpleGrantedAuthority::new)
                                .forEach(authorities::add);
                    }
                }
            }
        }

        return authorities;
    }
}
