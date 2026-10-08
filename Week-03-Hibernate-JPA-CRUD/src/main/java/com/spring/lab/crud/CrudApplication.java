package com.spring.lab.crud;

import com.spring.lab.crud.dao.StudentDAO;
import com.spring.lab.crud.entity.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class CrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

    /**
     * Demonstrates programmatic CLI interaction with Hibernate EntityManager.
     * Active only when running in non-test profiles.
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner commandLineRunner(StudentDAO studentDAO) {
        return runner -> {
            System.out.println(">>> Spring Boot 4 / Hibernate JPA CRUD Application Initialized <<<");
        };
    }
}
