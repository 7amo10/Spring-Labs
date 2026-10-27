package com.spring.lab.admin.controller;

import com.spring.lab.admin.entity.Employee;
import com.spring.lab.admin.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Spring MVC Controller orchestrating staff directory CRUD workflows with Post-Redirect-Get (PRG) patterns.
 */
@Controller
@RequestMapping("/staff")
public class StaffAdministrationController {

    private static final Logger log = LoggerFactory.getLogger(StaffAdministrationController.class);

    private final EmployeeService employeeService;

    @Autowired
    public StaffAdministrationController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Renders staff roster sorted alphabetically by last name.
     */
    @GetMapping({"", "/list"})
    public String listStaff(Model model) {
        List<Employee> staffList = employeeService.findAll();
        model.addAttribute("staffList", staffList);
        return "staff/staff-list";
    }

    /**
     * Displays empty form to register a new staff member.
     */
    @GetMapping("/addForm")
    public String showAddForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "staff/staff-form";
    }

    /**
     * Displays form pre-populated with existing staff entity data.
     */
    @GetMapping("/editForm")
    public String showEditForm(@RequestParam("staffId") int staffId, Model model) {
        Employee employee = employeeService.findById(staffId);
        model.addAttribute("employee", employee);
        return "staff/staff-form";
    }

    /**
     * Persists or updates staff entity and executes Post-Redirect-Get (PRG) to prevent duplicate submissions.
     */
    @PostMapping("/save")
    public String saveStaff(@ModelAttribute("employee") Employee employee) {
        log.info("Persisting staff member: {} {}, Department: {}",
                employee.getFirstName(), employee.getLastName(), employee.getDepartment());
        employeeService.save(employee);
        return "redirect:/staff/list";
    }

    /**
     * Safely deletes employee record by ID via POST and executes Post-Redirect-Get (PRG).
     */
    @PostMapping("/delete")
    public String deleteStaff(@RequestParam("staffId") int staffId) {
        log.warn("Deleting staff record with ID: {}", staffId);
        employeeService.deleteById(staffId);
        return "redirect:/staff/list";
    }
}
