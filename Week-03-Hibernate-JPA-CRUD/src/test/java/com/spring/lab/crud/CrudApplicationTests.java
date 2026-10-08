package com.spring.lab.crud;

import com.spring.lab.crud.dao.StudentDAO;
import com.spring.lab.crud.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CrudApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDAO studentDAO;

    @BeforeEach
    public void setup() {
        studentDAO.deleteAll();
    }

    @Test
    public void testDirectDAOPersistAndFindById() {
        Student student = new Student("Linus", "Torvalds", "linus@kernel.org");
        studentDAO.save(student);

        assertThat(student.getId()).isGreaterThan(0);

        Student found = studentDAO.findById(student.getId());
        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("Linus");
        assertThat(found.getLastName()).isEqualTo("Torvalds");
        assertThat(found.getEmail()).isEqualTo("linus@kernel.org");
    }

    @Test
    public void testDirectDAOFindAllAndQueryByLastName() {
        studentDAO.save(new Student("James", "Gosling", "james@java.com"));
        studentDAO.save(new Student("Ryan", "Gosling", "ryan@hollywood.com"));
        studentDAO.save(new Student("Dennis", "Ritchie", "dennis@c.org"));

        List<Student> all = studentDAO.findAll();
        assertThat(all).hasSize(3);

        List<Student> goslings = studentDAO.findByLastName("Gosling");
        assertThat(goslings).hasSize(2);
    }

    @Test
    public void testDirectDAOUpdateAndMerge() {
        Student student = new Student("Ada", "Lovelace", "ada@history.org");
        studentDAO.save(student);

        student.setEmail("ada.lovelace@computing.org");
        studentDAO.update(student);

        Student updated = studentDAO.findById(student.getId());
        assertThat(updated.getEmail()).isEqualTo("ada.lovelace@computing.org");
    }

    @Test
    public void testDirectDAODelete() {
        Student student = new Student("Alan", "Turing", "alan@bletchley.uk");
        studentDAO.save(student);
        int studentId = student.getId();

        studentDAO.delete(studentId);

        Student deleted = studentDAO.findById(studentId);
        assertThat(deleted).isNull();
    }

    @Test
    public void testRestCreateAndGetStudent() throws Exception {
        String studentJson = """
                {
                    "firstName": "Grace",
                    "lastName": "Hopper",
                    "email": "grace@navy.mil"
                }
                """;

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.firstName", is("Grace")))
                .andExpect(jsonPath("$.lastName", is("Hopper")))
                .andExpect(jsonPath("$.email", is("grace@navy.mil")));

        mockMvc.perform(get("/api/v1/students/search/lastName?lastName=Hopper"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Grace")));
    }

    @Test
    public void testRestUpdateStudent() throws Exception {
        Student student = new Student("Guido", "van Rossum", "guido@python.org");
        studentDAO.save(student);

        String updateJson = String.format("""
                {
                    "id": %d,
                    "firstName": "Guido",
                    "lastName": "van Rossum",
                    "email": "guido.benevolent@python.org"
                }
                """, student.getId());

        mockMvc.perform(put("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("guido.benevolent@python.org")));
    }

    @Test
    public void testRestDeleteStudent() throws Exception {
        Student student = new Student("Ken", "Thompson", "ken@bell-labs.com");
        studentDAO.save(student);

        mockMvc.perform(delete("/api/v1/students/" + student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        assertThat(studentDAO.findById(student.getId())).isNull();
    }
}
