package com.spring.lab.core.config;

import com.spring.lab.core.common.Coach;
import com.spring.lab.core.external.SwimCoach;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SportConfig {

    /**
     * Adapts external unmodifiable SwimCoach class into the Spring IoC container.
     * Declares custom lifecycle hooks without modifying SwimCoach source code.
     */
    @Bean(name = "swimCoach", initMethod = "init", destroyMethod = "cleanup")
    public Coach swimCoach() {
        return new SwimCoach();
    }
}
