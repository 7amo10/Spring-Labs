package com.spring.lab.mvc.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Jakarta Bean Validation constraint ensuring an email address
 * terminates with the designated corporate domain suffix.
 */
@Documented
@Constraint(validatedBy = CorporateEmailConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CorporateEmail {

    /**
     * Expected corporate domain suffix (e.g., "@company.org").
     */
    String domain() default "@company.org";

    /**
     * Localized error message when validation fails.
     */
    String message() default "must end with official domain @company.org";

    /**
     * Constraint validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload for client metadata association.
     */
    Class<? extends Payload>[] payload() default {};
}
