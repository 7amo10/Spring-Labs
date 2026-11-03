package com.spring.lab.academic.dao;

import com.spring.lab.academic.entity.Course;
import com.spring.lab.academic.entity.Instructor;
import com.spring.lab.academic.entity.InstructorDetail;
import com.spring.lab.academic.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Production implementation of AcademicDao using Jakarta Persistence EntityManager.
 * Encapsulates entity dissociation lifecycles and JPQL JOIN FETCH optimizations.
 */
@Repository
public class AcademicDaoImpl implements AcademicDao {

    private final EntityManager entityManager;

    @Autowired
    public AcademicDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // --- Instructor Operations ---

    @Override
    @Transactional
    public void saveInstructor(Instructor instructor) {
        entityManager.persist(instructor);
    }

    @Override
    public Instructor findInstructorById(int id) {
        return entityManager.find(Instructor.class, id);
    }

    @Override
    public Instructor findInstructorByIdJoinFetch(int id) {
        // Single optimized JPQL query loading instructor, detail, and courses at once
        TypedQuery<Instructor> query = entityManager.createQuery(
                "select i from Instructor i " +
                "JOIN FETCH i.instructorDetail " +
                "LEFT JOIN FETCH i.courses " +
                "where i.id = :id", Instructor.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateInstructor(Instructor instructor) {
        entityManager.merge(instructor);
    }

    @Override
    @Transactional
    public void deleteInstructorById(int id) {
        Instructor instructor = entityManager.find(Instructor.class, id);
        if (instructor != null) {
            // Break association with child courses so courses are not deleted
            if (instructor.getCourses() != null) {
                for (Course course : instructor.getCourses()) {
                    course.setInstructor(null);
                }
            }
            entityManager.remove(instructor);
        }
    }

    // --- Instructor Detail Operations ---

    @Override
    public InstructorDetail findInstructorDetailById(int id) {
        return entityManager.find(InstructorDetail.class, id);
    }

    @Override
    @Transactional
    public void deleteInstructorDetailById(int id) {
        InstructorDetail detail = entityManager.find(InstructorDetail.class, id);
        if (detail != null) {
            // Break bidirectional link to preserve parent instructor
            if (detail.getInstructor() != null) {
                detail.getInstructor().setInstructorDetail(null);
            }
            entityManager.remove(detail);
        }
    }

    // --- Course Operations ---

    @Override
    @Transactional
    public void saveCourse(Course course) {
        entityManager.persist(course);
    }

    @Override
    public Course findCourseById(int id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public List<Course> findCoursesByInstructorId(int instructorId) {
        TypedQuery<Course> query = entityManager.createQuery(
                "select c from Course c where c.instructor.id = :instructorId", Course.class);
        query.setParameter("instructorId", instructorId);
        return query.getResultList();
    }

    @Override
    public Course findCourseWithReviewsById(int id) {
        TypedQuery<Course> query = entityManager.createQuery(
                "select c from Course c " +
                "LEFT JOIN FETCH c.reviews " +
                "where c.id = :id", Course.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Course findCourseWithStudentsById(int id) {
        TypedQuery<Course> query = entityManager.createQuery(
                "select c from Course c " +
                "LEFT JOIN FETCH c.students " +
                "where c.id = :id", Course.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateCourse(Course course) {
        entityManager.merge(course);
    }

    @Override
    @Transactional
    public void deleteCourseById(int id) {
        Course course = entityManager.find(Course.class, id);
        if (course != null) {
            // Unlink from instructor if linked
            if (course.getInstructor() != null) {
                course.getInstructor().getCourses().remove(course);
                course.setInstructor(null);
            }
            // Reviews are deleted automatically due to CascadeType.ALL on reviews
            // Peer students remain unaffected
            entityManager.remove(course);
        }
    }

    // --- Student Operations ---

    @Override
    @Transactional
    public void saveStudent(Student student) {
        entityManager.persist(student);
    }

    @Override
    public Student findStudentById(int id) {
        return entityManager.find(Student.class, id);
    }

    @Override
    public Student findStudentWithCoursesById(int id) {
        TypedQuery<Student> query = entityManager.createQuery(
                "select s from Student s " +
                "LEFT JOIN FETCH s.courses " +
                "where s.id = :id", Student.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateStudent(Student student) {
        entityManager.merge(student);
    }

    @Override
    @Transactional
    public void deleteStudentById(int id) {
        Student student = entityManager.find(Student.class, id);
        if (student != null) {
            // Break association in course_student join table by removing from owning Course entities
            if (student.getCourses() != null) {
                for (Course course : student.getCourses()) {
                    course.getStudents().remove(student);
                }
            }
            entityManager.remove(student);
        }
    }
}
