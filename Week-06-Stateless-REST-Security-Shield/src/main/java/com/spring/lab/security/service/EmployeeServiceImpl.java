package com.spring.lab.security.service;

import com.spring.lab.security.dao.EmployeeRepository;
import com.spring.lab.security.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(int id) {
        Optional<Employee> result = employeeRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new RuntimeException("Employee id not found - " + id);
        }
    }

    @Override
    @Transactional
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee patch(int id, Employee patchData) {
        Employee employee = findById(id);

        if (patchData.getFirstName() != null) {
            employee.setFirstName(patchData.getFirstName());
        }
        if (patchData.getLastName() != null) {
            employee.setLastName(patchData.getLastName());
        }
        if (patchData.getEmail() != null) {
            employee.setEmail(patchData.getEmail());
        }

        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee id not found - " + id);
        }
        employeeRepository.deleteById(id);
    }
}
