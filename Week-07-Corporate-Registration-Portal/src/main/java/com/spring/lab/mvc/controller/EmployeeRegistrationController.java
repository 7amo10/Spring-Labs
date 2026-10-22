package com.spring.lab.mvc.controller;

import com.spring.lab.mvc.model.EmployeeRegistration;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Spring MVC controller managing corporate employee registration workflows,
 * form population from property configurations, and Bean Validation handling.
 */
@Controller
public class EmployeeRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeRegistrationController.class);

    @Value("${corporate.departments}")
    private List<String> departments;

    @Value("${corporate.employmentTypes}")
    private List<String> employmentTypes;

    @Value("${corporate.benefits}")
    private List<String> benefits;

    /**
     * Pre-process inbound web requests by registering a StringTrimmerEditor.
     * Trims leading/trailing whitespace and converts all-whitespace strings to null,
     * ensuring @NotNull and @Size validation constraints evaluate cleanly.
     */
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    /**
     * Renders the blank onboarding registration form.
     */
    @GetMapping({"/", "/register", "/registration/showForm"})
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("employee")) {
            model.addAttribute("employee", new EmployeeRegistration());
        }
        populateFormOptions(model);
        return "registration-form";
    }

    /**
     * Processes form submission, evaluates Bean Validation constraints,
     * and routes either to the confirmation view or back to the form with error feedback.
     */
    @PostMapping("/registration/processForm")
    public String processRegistration(
            @Valid @ModelAttribute("employee") EmployeeRegistration employee,
            BindingResult bindingResult,
            Model model) {

        log.info("Processing employee registration for: {} {}, email: {}",
                employee.getFirstName(), employee.getLastName(), employee.getCorporateEmail());

        if (bindingResult.hasErrors()) {
            log.warn("Validation failed with {} error(s): {}",
                    bindingResult.getErrorCount(), bindingResult.getAllErrors());
            populateFormOptions(model);
            return "registration-form";
        }

        log.info("Validation succeeded for employee: {} {}", employee.getFirstName(), employee.getLastName());
        return "registration-confirmation";
    }

    /**
     * Adds dynamic dropdown, radio, and checkbox choices into the Spring MVC Model.
     */
    private void populateFormOptions(Model model) {
        model.addAttribute("departments", departments);
        model.addAttribute("employmentTypes", employmentTypes);
        model.addAttribute("benefits", benefits);
    }
}
