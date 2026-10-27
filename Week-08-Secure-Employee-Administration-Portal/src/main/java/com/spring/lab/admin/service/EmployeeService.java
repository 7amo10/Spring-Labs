package com.spring.lab.admin.service;

import com.spring.lab.admin.entity.Employee;

import java.util.List;

/**
 * Service tier contract coordinating staff administrative workflows.
 */
public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(int id);

    Employee save(Employee employee);

    void deleteById(int id);
}
