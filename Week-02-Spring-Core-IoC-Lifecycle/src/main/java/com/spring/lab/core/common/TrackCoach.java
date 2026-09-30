package com.spring.lab.core.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TrackCoach implements Coach {

    private static final Logger logger = LoggerFactory.getLogger(TrackCoach.class);

    public TrackCoach() {
        logger.info("In constructor: {}", getClass().getSimpleName());
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
        return "Run a hard 5000-meter interval track workout.";
    }

    @Override
    public String getCoachType() {
        return "Track (@Primary Default Coach)";
    }
}
