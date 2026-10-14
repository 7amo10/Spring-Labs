package com.spring.lab.security.service;

import com.spring.lab.security.entity.Employee;

import java.util.List;

public interface EmployeeService {

    List<Employee> findAll();

    Employee findById(int id);

    Employee save(Employee employee);

    Employee patch(int id, Employee patchData);

    void deleteById(int id);
}
