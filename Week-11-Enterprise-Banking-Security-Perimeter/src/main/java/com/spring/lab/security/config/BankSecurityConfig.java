package com.spring.lab.security.config;

import com.spring.lab.security.exception.BankAccessDeniedHandler;
import com.spring.lab.security.exception.BankBasicAuthenticationEntryPoint;
import com.spring.lab.security.filter.CsrfCookieFilter;
import com.spring.lab.security.filter.SecurityAuditFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class BankSecurityConfig {

    private static final Set<String> KNOWN_COMPROMISED_PASSWORDS = Set.of(
            "12345678",
            "password123",
            "admin123",
            "bank12345",
            "welcome123",
            "qwerty123"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler csrfTokenRequestHandler = new CsrfTokenRequestAttributeHandler();

        http
            .securityContext(context -> context.requireExplicitSave(false))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRequestHandler(csrfTokenRequestHandler)
                .ignoringRequestMatchers(
                    "/api/v1/auth/register",
                    "/api/v1/support/inquiries",
                    "/h2-console/**"
                )
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            // Register Custom Filters into designated positions
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .addFilterBefore(new SecurityAuditFilter(), BasicAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Public Endpoints
                .requestMatchers(
                    "/api/v1/notices",
                    "/api/v1/support/inquiries",
                    "/api/v1/auth/register",
                    "/api/v1/auth/health",
                    "/h2-console/**"
                ).permitAll()

                // Admin Restricted Endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // Fine-Grained Authority Guarded Endpoints
                .requestMatchers("/api/v1/accounts/**").hasAnyAuthority("VIEW_ACCOUNT", "ROLE_ADMIN")
                .requestMatchers("/api/v1/transactions/**").hasAnyAuthority("VIEW_BALANCE", "ROLE_ADMIN")
                .requestMatchers("/api/v1/cards/**").hasAnyAuthority("VIEW_CARDS", "ROLE_ADMIN")
                .requestMatchers("/api/v1/loans/**").hasAnyAuthority("VIEW_LOANS", "ROLE_ADMIN")

                // General Authenticated Endpoints
                .requestMatchers("/api/v1/users/profile").authenticated()

                .anyRequest().authenticated()
            )
            .httpBasic(basic -> basic.authenticationEntryPoint(new BankBasicAuthenticationEntryPoint()))
            .exceptionHandling(ex -> ex.accessDeniedHandler(new BankAccessDeniedHandler()))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Spring Security 6.3+ CompromisedPasswordChecker
     * Checks passwords against known compromised dictionaries.
     */
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return password -> {
            if (password != null && KNOWN_COMPROMISED_PASSWORDS.contains(password.trim())) {
                return new CompromisedPasswordDecision(true);
            }
            return new CompromisedPasswordDecision(false);
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200", "http://127.0.0.1:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-XSRF-TOKEN",
                SecurityAuditFilter.TRACE_HEADER,
                BankBasicAuthenticationEntryPoint.ERROR_HEADER,
                BankAccessDeniedHandler.DENIED_HEADER
        ));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
