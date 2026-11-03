package com.spring.lab.academic.dao;

import com.spring.lab.academic.entity.Course;
import com.spring.lab.academic.entity.Instructor;
import com.spring.lab.academic.entity.InstructorDetail;
import com.spring.lab.academic.entity.Student;

import java.util.List;

/**
 * Data Access Object interface defining relational CRUD operations,
 * entity dissociation workflows, and JPQL JOIN FETCH fetch-optimized queries.
 */
public interface AcademicDao {

    // --- Instructor Operations ---
    void saveInstructor(Instructor instructor);

    Instructor findInstructorById(int id);

    Instructor findInstructorByIdJoinFetch(int id);

    void updateInstructor(Instructor instructor);

    void deleteInstructorById(int id);

    // --- Instructor Detail Operations ---
    InstructorDetail findInstructorDetailById(int id);

    void deleteInstructorDetailById(int id);

    // --- Course Operations ---
    void saveCourse(Course course);

    Course findCourseById(int id);

    List<Course> findCoursesByInstructorId(int instructorId);

    Course findCourseWithReviewsById(int id);

    Course findCourseWithStudentsById(int id);

    void updateCourse(Course course);

    void deleteCourseById(int id);

    // --- Student Operations ---
    void saveStudent(Student student);

    Student findStudentById(int id);

    Student findStudentWithCoursesById(int id);

    void updateStudent(Student student);

    void deleteStudentById(int id);
}
