package com.spring.lab.crud.service;

import com.spring.lab.crud.entity.Student;

import java.util.List;

public interface StudentService {

    Student save(Student student);

    Student findById(Integer id);

    List<Student> findAll();

    List<Student> findByLastName(String lastName);

    List<Student> findByEmail(String email);

    Student update(Student student);

    void delete(Integer id);

    int deleteAll();
}
