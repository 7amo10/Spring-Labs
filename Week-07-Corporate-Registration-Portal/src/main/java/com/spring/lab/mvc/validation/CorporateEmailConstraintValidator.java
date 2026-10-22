package com.spring.lab.mvc.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Constraint validator implementation enforcing corporate email domain suffix requirements.
 */
public class CorporateEmailConstraintValidator implements ConstraintValidator<CorporateEmail, String> {

    private String requiredDomain;

    @Override
    public void initialize(CorporateEmail corporateEmail) {
        this.requiredDomain = corporateEmail.domain();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Allow null/empty values to be handled by @NotNull or @Size if configured
        if (email == null || email.trim().isEmpty()) {
            return true;
        }

        return email.toLowerCase().endsWith(requiredDomain.toLowerCase());
    }
}
