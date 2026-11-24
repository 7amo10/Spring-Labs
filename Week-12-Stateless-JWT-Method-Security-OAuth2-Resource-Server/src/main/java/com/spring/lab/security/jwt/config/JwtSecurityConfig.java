package com.spring.lab.security.jwt.config;

import com.spring.lab.security.jwt.exception.BankAccessDeniedHandler;
import com.spring.lab.security.jwt.exception.BankAuthenticationEntryPoint;
import com.spring.lab.security.jwt.filter.*;
import com.spring.lab.security.jwt.provider.BankUsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class JwtSecurityConfig {

    @Value("${application.security.jwt.secret:" + SecurityConstants.JWT_SECRET_DEFAULT_VALUE + "}")
    private String jwtSecret;

    private final BankAuthenticationEntryPoint authenticationEntryPoint;
    private final BankAccessDeniedHandler accessDeniedHandler;

    public JwtSecurityConfig(BankAuthenticationEntryPoint authenticationEntryPoint, BankAccessDeniedHandler accessDeniedHandler) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(BankUsernamePasswordAuthenticationProvider authenticationProvider) {
        ProviderManager providerManager = new ProviderManager(Collections.singletonList(authenticationProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public JwtAuthenticationConverter keycloakJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.apexbank.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        config.setExposedHeaders(List.of(SecurityConstants.JWT_HEADER));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Security Filter Chain 1: OAuth2 Federated Identity & Resource Server (Sections 14-16)
     * Protects endpoints under /api/v1/federated/** using remote or mock Keycloak JWT validation.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain federatedResourceServerFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/v1/federated/**")
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/federated/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter())))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    /**
     * Security Filter Chain 2: Primary Stateless JWT Perimeter Gateway (Sections 9-12)
     * Enforces Stateless sessions, custom filter ordering, HMAC-SHA JWT validation & issuance.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain primaryJwtSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(fo -> fo.disable()))
                .addFilterBefore(new RequestValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidatorFilter(jwtSecret), BasicAuthenticationFilter.class)
                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(jwtSecret), BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/notices",
                                "/h2-console/**",
                                "/actuator/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/login", "/api/v1/user/profile").authenticated()
                        .requestMatchers("/api/v1/accounts/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/cards/**").hasRole("USER")
                        .requestMatchers("/api/v1/loans/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/transactions/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/inquiries/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }
}
