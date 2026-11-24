package com.spring.lab.security.jwt;

import com.spring.lab.security.jwt.config.KeycloakRoleConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter roleConverter = new KeycloakRoleConverter();

    @Test
    @DisplayName("Should extract realm roles and map them with ROLE_ prefix")
    void testConvertRealmRoles() {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("user", "admin", "auditor"));
        claims.put("realm_access", realmAccess);

        Jwt jwt = new Jwt(
                "mock-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                claims
        );

        Collection<GrantedAuthority> authorities = roleConverter.convert(jwt);

        assertThat(authorities).isNotNull();
        List<String> authorityNames = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        assertThat(authorityNames).containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN", "ROLE_AUDITOR");
    }

    @Test
    @DisplayName("Should return empty collection when no realm_access or resource_access is present")
    void testConvertWithoutRoles() {
        Jwt jwt = new Jwt(
                "token-without-roles",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user-without-roles")
        );

        Collection<GrantedAuthority> authorities = roleConverter.convert(jwt);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should handle null jwt input gracefully")
    void testConvertNullJwt() {
        Collection<GrantedAuthority> authorities = roleConverter.convert(null);
        assertThat(authorities).isEmpty();
    }
}
