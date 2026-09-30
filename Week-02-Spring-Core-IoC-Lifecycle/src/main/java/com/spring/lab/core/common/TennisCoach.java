package com.spring.lab.core.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class TennisCoach implements Coach {

    private static final Logger logger = LoggerFactory.getLogger(TennisCoach.class);

    public TennisCoach() {
        logger.info("In constructor: {} (Deferred by @Lazy)", getClass().getSimpleName());
    }

    @PostConstruct
    public void init() {
        logger.info("In @PostConstruct lifecycle hook: {}", getClass().getSimpleName());
    }

    @PreDestroy
    public void cleanup() {
        logger.info("In @PreDestroy lifecycle hook: {}", getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Practice backhand volleys and second serves for 30 minutes.";
    }

    @Override
    public String getCoachType() {
        return "Tennis (@Lazy Initialized)";
    }
}
