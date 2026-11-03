package com.spring.lab.academic;

import com.spring.lab.academic.dao.AcademicDao;
import com.spring.lab.academic.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class AcademicApplication {

    private static final Logger log = LoggerFactory.getLogger(AcademicApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AcademicApplication.class, args);
    }

    /**
     * Interactive console demonstration runner active only outside the test profile.
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(AcademicDao academicDao) {
        return args -> {
            log.info("=== Executing Academic Domain Lifecycle Demonstration ===");

            // 1. Create Instructor with Detail and Courses (1:1 and 1:N)
            Instructor instructor = new Instructor("Linus", "Torvalds", "linus@kernel.org");
            InstructorDetail detail = new InstructorDetail("https://kernel.org", "Operating Systems Architecture");
            instructor.setInstructorDetail(detail);

            Course course1 = new Course("Advanced Operating Systems", "CS-501", 4);
            Course course2 = new Course("Linux Kernel Internals", "CS-601", 4);
            instructor.addCourse(course1);
            instructor.addCourse(course2);

            log.info("Persisting Instructor with 1:1 Detail and 1:N Courses...");
            academicDao.saveInstructor(instructor);

            // 2. Add Unidirectional 1:N Reviews to Course
            course1.addReview(new Review(5, "Incredible depth on monolithic kernel design!"));
            course1.addReview(new Review(4, "Challenging labs, highly rewarding."));
            academicDao.updateCourse(course1);

            // 3. Create Students and Enroll in Courses (M:N)
            Student student1 = new Student("Alan", "Turing", "alan@cambridge.edu", "Computer Science");
            Student student2 = new Student("Ada", "Lovelace", "ada@analytical.org", "Mathematics");
            academicDao.saveStudent(student1);
            academicDao.saveStudent(student2);

            course1.addStudent(student1);
            course1.addStudent(student2);
            course2.addStudent(student1);
            academicDao.updateCourse(course1);
            academicDao.updateCourse(course2);

            // 4. Test JOIN FETCH Optimization
            log.info("Executing JOIN FETCH query to prevent N+1 query proliferation...");
            Instructor fetchedInstructor = academicDao.findInstructorByIdJoinFetch(instructor.getId());
            log.info("Loaded Instructor via 1 SQL query: {} with {} courses",
                    fetchedInstructor.getFirstName() + " " + fetchedInstructor.getLastName(),
                    fetchedInstructor.getCourses().size());

            log.info("=== Academic Domain Demonstration Completed Successfully ===");
        };
    }
}
