package com.spring.lab.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

/**
 * Spring Security configuration establishing relational JDBC authentication,
 * custom Bootstrap form-based login, logout handling, and fine-grained RBAC policies.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures JDBC UserDetailsManager with custom queries targeting 'members' and 'roles' tables.
     */
    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

        jdbcUserDetailsManager.setUsersByUsernameQuery(
                "select user_id, pw, active from members where user_id=?"
        );

        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_id, role from roles where user_id=?"
        );

        return jdbcUserDetailsManager;
    }

    /**
     * Enforces perimeter defenses: form login, custom login/access-denied views,
     * CSRF protection for web forms, and verb/route-level RBAC restrictions.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/login", "/css/**", "/js/**", "/actuator/**").permitAll()
                        .requestMatchers("/", "/staff", "/staff/list").hasRole("EMPLOYEE")
                        .requestMatchers("/staff/addForm", "/staff/editForm", "/staff/save").hasRole("MANAGER")
                        .requestMatchers("/staff/delete").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );

        http.formLogin(form ->
                form
                        .loginPage("/login")
                        .loginProcessingUrl("/authenticate")
                        .defaultSuccessUrl("/staff/list", true)
                        .permitAll()
        );

        http.logout(logout ->
                logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
        );

        http.exceptionHandling(configurer ->
                configurer.accessDeniedPage("/access-denied")
        );

        return http.build();
    }
}
