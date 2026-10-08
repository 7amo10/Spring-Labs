package com.spring.lab.crud.service;

import com.spring.lab.crud.dao.StudentDAO;
import com.spring.lab.crud.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentDAO studentDAO;

    @Autowired
    public StudentServiceImpl(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    @Override
    @Transactional
    public Student save(Student student) {
        studentDAO.save(student);
        return student;
    }

    @Override
    public Student findById(Integer id) {
        Student student = studentDAO.findById(id);
        if (student == null) {
            throw new RuntimeException("Student id not found - " + id);
        }
        return student;
    }

    @Override
    public List<Student> findAll() {
        return studentDAO.findAll();
    }

    @Override
    public List<Student> findByLastName(String lastName) {
        return studentDAO.findByLastName(lastName);
    }

    @Override
    public List<Student> findByEmail(String email) {
        return studentDAO.findByEmail(email);
    }

    @Override
    @Transactional
    public Student update(Student student) {
        studentDAO.update(student);
        return student;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        studentDAO.delete(id);
    }

    @Override
    @Transactional
    public int deleteAll() {
        return studentDAO.deleteAll();
    }
}
