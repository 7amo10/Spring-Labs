package com.spring.lab.admin.dao;

import com.spring.lab.admin.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository providing automated persistence queries for Employee records.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * Derived query method sorting employees alphabetically by last name.
     */
    List<Employee> findAllByOrderByLastNameAsc();
}
