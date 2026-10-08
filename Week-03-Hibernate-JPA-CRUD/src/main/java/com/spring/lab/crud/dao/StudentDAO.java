package com.spring.lab.crud.dao;

import com.spring.lab.crud.entity.Student;

import java.util.List;

public interface StudentDAO {

    void save(Student theStudent);

    Student findById(Integer id);

    List<Student> findAll();

    List<Student> findByLastName(String theLastName);

    List<Student> findByEmail(String theEmail);

    void update(Student theStudent);

    void delete(Integer id);

    int deleteAll();
}
