package com.spring.lab.core.external;

import com.spring.lab.core.common.Coach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simulates a legacy / third-party unmodifiable library class.
 * Notice: NO Spring annotations (@Component, @Service, etc.) are present here.
 */
public class SwimCoach implements Coach {

    private static final Logger logger = LoggerFactory.getLogger(SwimCoach.class);

    public SwimCoach() {
        logger.info("In constructor: {} (External 3rd-Party POJO)", getClass().getSimpleName());
    }

    public void init() {
        logger.info("In custom init-method callback: {} (Configured via @Bean)", getClass().getSimpleName());
    }

    public void cleanup() {
        logger.info("In custom destroy-method callback: {} (Configured via @Bean)", getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Swim 1,000 meters as a warm-up followed by 10x50m freestyle sprints.";
    }

    @Override
    public String getCoachType() {
        return "Swim (Adapted 3rd-Party Bean via Java Config)";
    }
}
