package com.spring.lab.repository.dao;

import com.spring.lab.repository.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "members", collectionResourceRel = "members")
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    // Derived Query Method: Search by exact last name
    @RestResource(path = "by-last-name", rel = "by-last-name")
    List<Employee> findByLastName(@Param("lastName") String lastName);

    // Derived Query Method: Search by partial email containing keyword with pagination support
    @RestResource(path = "by-email-containing", rel = "by-email-containing")
    Page<Employee> findByEmailContaining(@Param("keyword") String keyword, Pageable pageable);
}
