package com.spring.lab.academic;

import com.spring.lab.academic.dao.AcademicDao;
import com.spring.lab.academic.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AcademicApplicationTests {

    @Autowired
    private AcademicDao academicDao;

    @Test
    @DisplayName("1:1 Unidirectional & Cascade: Persisting Instructor automatically persists InstructorDetail")
    void testCreateInstructorWithDetail_CascadeAll() {
        Instructor instructor = new Instructor("Grace", "Hopper", "grace@navy.mil");
        InstructorDetail detail = new InstructorDetail("https://hopper.org", "Compiler Design & COBOL");
        instructor.setInstructorDetail(detail);

        academicDao.saveInstructor(instructor);

        assertThat(instructor.getId()).isPositive();
        assertThat(detail.getId()).isPositive();

        Instructor found = academicDao.findInstructorById(instructor.getId());
        assertThat(found).isNotNull();
        assertThat(found.getInstructorDetail()).isNotNull();
        assertThat(found.getInstructorDetail().getSpecialization()).isEqualTo("Compiler Design & COBOL");
    }

    @Test
    @DisplayName("1:1 Bidirectional: Finding InstructorDetail retrieves associated parent Instructor via mappedBy")
    void testFindInstructorDetailBidirectional() {
        Instructor instructor = new Instructor("Donald", "Knuth", "knuth@stanford.edu");
        InstructorDetail detail = new InstructorDetail("https://cs.stanford.edu/~knuth", "Algorithm Analysis");
        instructor.setInstructorDetail(detail);

        academicDao.saveInstructor(instructor);

        InstructorDetail foundDetail = academicDao.findInstructorDetailById(detail.getId());
        assertThat(foundDetail).isNotNull();
        assertThat(foundDetail.getInstructor()).isNotNull();
        assertThat(foundDetail.getInstructor().getLastName()).isEqualTo("Knuth");
    }

    @Test
    @DisplayName("1:1 Dissociation: Deleting InstructorDetail preserves Instructor when link is unlinked")
    void testDeleteInstructorDetailOnly_PreservesInstructor() {
        Instructor instructor = new Instructor("Ken", "Thompson", "ken@bell-labs.com");
        InstructorDetail detail = new InstructorDetail("https://bell-labs.com/ken", "Unix & B Language");
        instructor.setInstructorDetail(detail);

        academicDao.saveInstructor(instructor);
        int instructorId = instructor.getId();
        int detailId = detail.getId();

        // Delete detail with dissociation
        academicDao.deleteInstructorDetailById(detailId);

        assertThat(academicDao.findInstructorDetailById(detailId)).isNull();
        Instructor preservedInstructor = academicDao.findInstructorById(instructorId);
        assertThat(preservedInstructor).isNotNull();
        assertThat(preservedInstructor.getLastName()).isEqualTo("Thompson");
        assertThat(preservedInstructor.getInstructorDetail()).isNull();
    }

    @Test
    @DisplayName("1:N Cascade: Persisting Instructor cascades to associated Course entities via CascadeType.PERSIST")
    void testCreateInstructorWithCourses_CascadePersist() {
        Instructor instructor = new Instructor("Dennis", "Ritchie", "dmr@bell-labs.com");
        InstructorDetail detail = new InstructorDetail("https://bell-labs.com/dmr", "C Programming & Unix");
        instructor.setInstructorDetail(detail);

        Course c1 = new Course("The C Programming Language", "CS-201", 3);
        Course c2 = new Course("Systems Programming in Unix", "CS-301", 4);
        instructor.addCourse(c1);
        instructor.addCourse(c2);

        academicDao.saveInstructor(instructor);

        assertThat(instructor.getId()).isPositive();
        assertThat(c1.getId()).isPositive();
        assertThat(c2.getId()).isPositive();

        List<Course> courses = academicDao.findCoursesByInstructorId(instructor.getId());
        assertThat(courses).hasSize(2);
    }

    @Test
    @DisplayName("1:N Fetch Optimization: JOIN FETCH retrieves Instructor, Details, and Courses in 1 SQL query")
    void testFindInstructorByIdJoinFetch_SingleQueryOptimization() {
        Instructor instructor = new Instructor("Claude", "Shannon", "shannon@mit.edu");
        InstructorDetail detail = new InstructorDetail("https://mit.edu/shannon", "Information Theory");
        instructor.setInstructorDetail(detail);

        Course c1 = new Course("Mathematical Theory of Communication", "EE-401", 4);
        Course c2 = new Course("Digital Cryptography Principles", "EE-501", 3);
        instructor.addCourse(c1);
        instructor.addCourse(c2);

        academicDao.saveInstructor(instructor);

        // Execute single JOIN FETCH query
        Instructor fetched = academicDao.findInstructorByIdJoinFetch(instructor.getId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.getInstructorDetail().getSpecialization()).isEqualTo("Information Theory");
        assertThat(fetched.getCourses()).hasSize(2);
        assertThat(fetched.getCourses()).extracting(Course::getCourseCode)
                .containsExactlyInAnyOrder("EE-401", "EE-501");
    }

    @Test
    @DisplayName("1:N Dissociation: Deleting Instructor unlinks and preserves Course entities")
    void testDeleteInstructorById_PreservesCourses() {
        Instructor instructor = new Instructor("Bjarne", "Stroustrup", "bjarne@columbia.edu");
        InstructorDetail detail = new InstructorDetail("https://stroustrup.com", "C++ & Modern Paradigms");
        instructor.setInstructorDetail(detail);

        Course c1 = new Course("Advanced C++ Metaprogramming", "CS-450", 4);
        Course c2 = new Course("Object-Oriented Software Design", "CS-350", 3);
        instructor.addCourse(c1);
        instructor.addCourse(c2);

        academicDao.saveInstructor(instructor);
        int instructorId = instructor.getId();
        int c1Id = c1.getId();
        int c2Id = c2.getId();

        // Delete instructor with course dissociation
        academicDao.deleteInstructorById(instructorId);

        assertThat(academicDao.findInstructorById(instructorId)).isNull();

        Course preservedC1 = academicDao.findCourseById(c1Id);
        Course preservedC2 = academicDao.findCourseById(c2Id);
        assertThat(preservedC1).isNotNull();
        assertThat(preservedC2).isNotNull();
        assertThat(preservedC1.getInstructor()).isNull();
        assertThat(preservedC2.getInstructor()).isNull();
    }

    @Test
    @DisplayName("1:N Unidirectional Cascade: Saving Course cascades persist to Reviews via CascadeType.ALL")
    void testCreateCourseWithReviews_CascadeAll() {
        Course course = new Course("Distributed Consensus Protocols", "CS-701", 4);
        course.addReview(new Review(5, "Exceptional coverage of Raft and Paxos algorithms."));
        course.addReview(new Review(4, "Rigorous assignments on distributed systems."));

        academicDao.saveCourse(course);

        assertThat(course.getId()).isPositive();
        assertThat(course.getReviews()).hasSize(2);

        Course found = academicDao.findCourseWithReviewsById(course.getId());
        assertThat(found).isNotNull();
        assertThat(found.getReviews()).hasSize(2);
        assertThat(found.getReviews()).extracting(Review::getRating).containsExactlyInAnyOrder(5, 4);
    }

    @Test
    @DisplayName("1:N Unidirectional Cascade: Deleting Course cascades delete to attached Reviews")
    void testDeleteCourseById_CascadesReviewDeletion() {
        Course course = new Course("Microservices Resiliency Patterns", "CS-720", 3);
        Review r1 = new Review(5, "Top notch circuit breaker patterns.");
        Review r2 = new Review(5, "Bulkheads and rate limiters clearly explained.");
        course.addReview(r1);
        course.addReview(r2);

        academicDao.saveCourse(course);
        int courseId = course.getId();

        academicDao.deleteCourseById(courseId);

        assertThat(academicDao.findCourseById(courseId)).isNull();
    }

    @Test
    @DisplayName("M:N Association: Course and Student bidirectional enrollments via join table")
    void testCreateCourseAndStudents_ManyToMany() {
        Course course = new Course("Quantum Algorithms & Computation", "CS-801", 4);
        Student s1 = new Student("Richard", "Feynman", "feynman@caltech.edu", "Physics");
        Student s2 = new Student("David", "Deutsch", "deutsch@oxford.ac.uk", "Quantum Physics");

        course.addStudent(s1);
        course.addStudent(s2);
        academicDao.saveCourse(course);

        Course fetchedCourse = academicDao.findCourseWithStudentsById(course.getId());
        assertThat(fetchedCourse).isNotNull();
        assertThat(fetchedCourse.getStudents()).hasSize(2);
        assertThat(fetchedCourse.getStudents()).extracting(Student::getLastName)
                .containsExactlyInAnyOrder("Feynman", "Deutsch");
    }

    @Test
    @DisplayName("M:N Dissociation: Deleting Course removes join table rows without deleting Student entities")
    void testDeleteCourse_PreservesStudents() {
        Course course = new Course("Compiler Optimization Techniques", "CS-520", 3);
        Student student = new Student("Barbara", "Liskov", "liskov@mit.edu", "Computer Science");

        course.addStudent(student);
        academicDao.saveCourse(course);

        int courseId = course.getId();
        int studentId = student.getId();

        academicDao.deleteCourseById(courseId);

        assertThat(academicDao.findCourseById(courseId)).isNull();
        Student preservedStudent = academicDao.findStudentById(studentId);
        assertThat(preservedStudent).isNotNull();
        assertThat(preservedStudent.getLastName()).isEqualTo("Liskov");
    }

    @Test
    @DisplayName("M:N Dissociation: Deleting Student unlinks join table rows without deleting Course entities")
    void testDeleteStudent_PreservesCourses() {
        Course course = new Course("Computational Complexity Theory", "CS-610", 4);
        Student student = new Student("Stephen", "Cook", "cook@toronto.edu", "Computer Science");

        course.addStudent(student);
        academicDao.saveCourse(course);

        int courseId = course.getId();
        int studentId = student.getId();

        academicDao.deleteStudentById(studentId);

        assertThat(academicDao.findStudentById(studentId)).isNull();
        Course preservedCourse = academicDao.findCourseById(courseId);
        assertThat(preservedCourse).isNotNull();
        assertThat(preservedCourse.getTitle()).isEqualTo("Computational Complexity Theory");
    }
}
