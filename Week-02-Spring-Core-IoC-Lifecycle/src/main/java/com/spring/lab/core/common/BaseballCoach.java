package com.spring.lab.core.common;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BaseballCoach implements Coach {

    private static final Logger logger = LoggerFactory.getLogger(BaseballCoach.class);
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger(0);

    private final int instanceId;

    public BaseballCoach() {
        this.instanceId = INSTANCE_COUNTER.incrementAndGet();
        logger.info("In constructor: {} [Instance ID: #{}]", getClass().getSimpleName(), instanceId);
    }

    @PostConstruct
    public void init() {
        logger.info("In @PostConstruct lifecycle hook: {} [Instance ID: #{}]", getClass().getSimpleName(), instanceId);
    }

    public int getInstanceId() {
        return instanceId;
    }

    @Override
    public String getDailyWorkout() {
        return "Spend 30 minutes in the batting cage working on high fastballs and curveballs.";
    }

    @Override
    public String getCoachType() {
        return "Baseball (Prototype Scope, Instance #" + instanceId + ")";
    }
}
