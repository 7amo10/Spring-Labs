package com.spring.lab.core.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CricketCoach implements Coach {

    private static final Logger logger = LoggerFactory.getLogger(CricketCoach.class);

    public CricketCoach() {
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
        return "Practice fast bowling and defensive batting for 15 minutes.";
    }

    @Override
    public String getCoachType() {
        return "Cricket (Standard Singleton)";
    }
}
