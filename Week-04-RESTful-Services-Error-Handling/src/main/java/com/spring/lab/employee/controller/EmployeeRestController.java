package com.spring.lab.employee.controller;

import com.spring.lab.employee.entity.Employee;
import com.spring.lab.employee.exception.EmployeeNotFoundException;
import com.spring.lab.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {
        Employee employee = employeeService.findById(employeeId);
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee id not found - " + employeeId);
        }
        return employee;
    }

    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee employee) {
        // Enforce ID 0 for new record insertion instead of update
        employee.setId(0);
        return employeeService.save(employee);
    }

    @PutMapping("/employees")
    public Employee updateEmployee(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @PatchMapping("/employees/{employeeId}")
    public Employee patchEmployee(@PathVariable int employeeId, @RequestBody Map<String, Object> patchPayload) {
        Employee tempEmployee = employeeService.findById(employeeId);
        if (tempEmployee == null) {
            throw new EmployeeNotFoundException("Employee id not found - " + employeeId);
        }

        if (patchPayload.containsKey("id")) {
            throw new IllegalArgumentException("Employee id cannot be modified. Remove 'id' from request body.");
        }

        if (patchPayload.containsKey("firstName")) {
            tempEmployee.setFirstName((String) patchPayload.get("firstName"));
        }
        if (patchPayload.containsKey("lastName")) {
            tempEmployee.setLastName((String) patchPayload.get("lastName"));
        }
        if (patchPayload.containsKey("email")) {
            tempEmployee.setEmail((String) patchPayload.get("email"));
        }

        return employeeService.save(tempEmployee);
    }

    @DeleteMapping("/employees/{employeeId}")
    public Map<String, Object> deleteEmployee(@PathVariable int employeeId) {
        Employee tempEmployee = employeeService.findById(employeeId);
        if (tempEmployee == null) {
            throw new EmployeeNotFoundException("Employee id not found - " + employeeId);
        }

        employeeService.deleteById(employeeId);

        return Map.of(
                "status", "SUCCESS",
                "message", "Deleted employee id - " + employeeId
        );
    }
}
