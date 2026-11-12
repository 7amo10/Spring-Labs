package com.spring.lab.aop.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class ArchitecturePointcuts {

    // Matches all execution points within the REST controller tier
    @Pointcut("execution(* com.spring.lab.aop.rest.*.*(..))")
    public void forControllerPackage() {}

    // Matches all execution points within the Service business tier
    @Pointcut("execution(* com.spring.lab.aop.service.*.*(..))")
    public void forServicePackage() {}

    // Matches all execution points within the DAO data tier
    @Pointcut("execution(* com.spring.lab.aop.dao.*.*(..))")
    public void forDaoPackage() {}

    // Matches getter methods in the DAO tier
    @Pointcut("execution(* com.spring.lab.aop.dao.*.get*(..))")
    public void getter() {}

    // Matches setter methods in the DAO tier
    @Pointcut("execution(* com.spring.lab.aop.dao.*.set*(..))")
    public void setter() {}

    // Composite pointcut: DAO tier excluding noisy getter and setter operations
    @Pointcut("forDaoPackage() && !(getter() || setter())")
    public void forDaoNoGetterSetter() {}

    // Composite pointcut: entire enterprise application flow across all tiers (excluding DAO getters/setters)
    @Pointcut("forControllerPackage() || forServicePackage() || forDaoNoGetterSetter()")
    public void forAppFlow() {}

    // Pointcut targeting payment settlement service methods
    @Pointcut("execution(* com.spring.lab.aop.service.PaymentSettlementService.*(..))")
    public void forSettlementService() {}
}
