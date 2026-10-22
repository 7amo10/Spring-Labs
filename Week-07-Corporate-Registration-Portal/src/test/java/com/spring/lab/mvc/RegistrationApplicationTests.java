package com.spring.lab.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / should display registration form with populated dropdown and options")
    void testShowRegistrationForm_Success() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeExists("employee", "departments", "employmentTypes", "benefits"))
                .andExpect(model().attribute("departments", hasItems("Engineering", "Cybersecurity")))
                .andExpect(content().string(containsString("Corporate Onboarding Portal")));
    }

    @Test
    @DisplayName("GET /register and /registration/showForm should route to registration form")
    void testShowRegistrationForm_AlternateRoutes() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"));

        mockMvc.perform(get("/registration/showForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"));
    }

    @Test
    @DisplayName("POST /registration/processForm with valid data routes to confirmation view")
    void testProcessRegistration_Success() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "Alexandra")
                        .param("lastName", "Vance")
                        .param("corporateEmail", "alexandra.vance@company.org")
                        .param("postalCode", "94105")
                        .param("salary", "95000")
                        .param("department", "Engineering")
                        .param("employmentType", "Full-Time Permanent")
                        .param("selectedBenefits", "Comprehensive Health & Dental", "401(k) Match up to 6%"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-confirmation"))
                .andExpect(model().hasNoErrors())
                .andExpect(content().string(containsString("Alexandra Vance")))
                .andExpect(content().string(containsString("alexandra.vance@company.org")))
                .andExpect(content().string(containsString("95,000.00")));
    }

    @Test
    @DisplayName("StringTrimmerEditor trims whitespace-only lastName and triggers @NotNull/@Size error")
    void testProcessRegistration_FailsWhenLastNameWhitespace() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "   ") // Only spaces, should be trimmed to null
                        .param("corporateEmail", "john.doe@company.org")
                        .param("postalCode", "94105")
                        .param("salary", "85000"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "lastName"))
                .andExpect(content().string(containsString("is required")));
    }

    @Test
    @DisplayName("Custom @CorporateEmail validator rejects non-company domain suffix")
    void testProcessRegistration_FailsWhenCorporateEmailInvalid() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("corporateEmail", "john.doe@gmail.com") // Invalid domain
                        .param("postalCode", "94105")
                        .param("salary", "85000"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "corporateEmail"))
                .andExpect(content().string(containsString("must end with official domain @company.org")));
    }

    @Test
    @DisplayName("Custom @CorporateEmail validator accepts uppercase corporate domain")
    void testProcessRegistration_AcceptsUppercaseDomain() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "Sarah")
                        .param("lastName", "Connor")
                        .param("corporateEmail", "sarah.connor@COMPANY.ORG") // Uppercase domain
                        .param("postalCode", "94105")
                        .param("salary", "110000"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-confirmation"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    @DisplayName("Postal code regex rejects non-5-alphanumeric formats")
    void testProcessRegistration_FailsWhenPostalCodeInvalid() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("corporateEmail", "john.doe@company.org")
                        .param("postalCode", "123") // Invalid length
                        .param("salary", "85000"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "postalCode"))
                .andExpect(content().string(containsString("must be exactly 5 alphanumeric characters")));
    }

    @Test
    @DisplayName("Salary validation rejects values below 30,000")
    void testProcessRegistration_FailsWhenSalaryBelowMinimum() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("corporateEmail", "john.doe@company.org")
                        .param("postalCode", "94105")
                        .param("salary", "15000")) // Below min 30,000
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "salary"))
                .andExpect(content().string(containsString("must be greater than or equal to 30,000")));
    }

    @Test
    @DisplayName("Salary validation rejects values exceeding 250,000")
    void testProcessRegistration_FailsWhenSalaryAboveMaximum() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("corporateEmail", "john.doe@company.org")
                        .param("postalCode", "94105")
                        .param("salary", "350000")) // Above max 250,000
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "salary"))
                .andExpect(content().string(containsString("must be less than or equal to 250,000")));
    }

    @Test
    @DisplayName("Non-numeric salary triggers typeMismatch error message from messages.properties")
    void testProcessRegistration_FailsOnTypeMismatch() throws Exception {
        mockMvc.perform(post("/registration/processForm")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("corporateEmail", "john.doe@company.org")
                        .param("postalCode", "94105")
                        .param("salary", "eighty-five-thousand")) // Non-integer
                .andExpect(status().isOk())
                .andExpect(view().name("registration-form"))
                .andExpect(model().attributeHasFieldErrors("employee", "salary"))
                .andExpect(content().string(containsString("Invalid numerical amount. Please enter a valid whole salary value.")));
    }
}
