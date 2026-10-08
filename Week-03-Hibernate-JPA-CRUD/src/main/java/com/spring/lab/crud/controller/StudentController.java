package com.spring.lab.crud.controller;

import com.spring.lab.crud.entity.Student;
import com.spring.lab.crud.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }

    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable int studentId) {
        return studentService.findById(studentId);
    }

    @GetMapping("/search/lastName")
    public List<Student> getStudentsByLastName(@RequestParam String lastName) {
        return studentService.findByLastName(lastName);
    }

    @GetMapping("/search/email")
    public List<Student> getStudentsByEmail(@RequestParam String email) {
        return studentService.findByEmail(email);
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        // Enforce ID 0 for new record insertion
        student.setId(0);
        return studentService.save(student);
    }

    @PutMapping
    public Student updateStudent(@RequestBody Student student) {
        return studentService.update(student);
    }

    @DeleteMapping("/{studentId}")
    public Map<String, Object> deleteStudent(@PathVariable int studentId) {
        studentService.findById(studentId); // Throws exception if not found
        studentService.delete(studentId);
        return Map.of(
                "status", "SUCCESS",
                "message", "Deleted student id: " + studentId
        );
    }

    @DeleteMapping
    public Map<String, Object> deleteAllStudents() {
        int deletedCount = studentService.deleteAll();
        return Map.of(
                "status", "SUCCESS",
                "deletedCount", deletedCount,
                "message", "Deleted all students from tracker"
        );
    }
}
