package com.spring.lab.employee;

import com.spring.lab.employee.entity.Employee;
import com.spring.lab.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @BeforeEach
    public void setup() {
        for (Employee employee : employeeService.findAll()) {
            employeeService.deleteById(employee.getId());
        }
    }

    @Test
    public void testFindAllEmployees() throws Exception {
        employeeService.save(new Employee("Leslie", "Andrews", "leslie@luv2code.com"));
        employeeService.save(new Employee("Emma", "Baumgarten", "emma@luv2code.com"));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Leslie")))
                .andExpect(jsonPath("$[1].firstName", is("Emma")));
    }

    @Test
    public void testGetEmployeeByIdSuccess() throws Exception {
        Employee saved = employeeService.save(new Employee("Yuri", "Petrov", "yuri@luv2code.com"));

        mockMvc.perform(get("/api/employees/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId())))
                .andExpect(jsonPath("$.firstName", is("Yuri")))
                .andExpect(jsonPath("$.email", is("yuri@luv2code.com")));
    }

    @Test
    public void testGetEmployeeByIdNotFoundGlobalException() throws Exception {
        mockMvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Employee id not found - 9999")))
                .andExpect(jsonPath("$.timeStamp", notNullValue()));
    }

    @Test
    public void testCreateEmployee() throws Exception {
        String employeeJson = """
                {
                    "firstName": "Juan",
                    "lastName": "Vega",
                    "email": "juan@luv2code.com"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.firstName", is("Juan")))
                .andExpect(jsonPath("$.lastName", is("Vega")))
                .andExpect(jsonPath("$.email", is("juan@luv2code.com")));
    }

    @Test
    public void testUpdateEmployee() throws Exception {
        Employee saved = employeeService.save(new Employee("Avani", "Gupta", "avani@luv2code.com"));

        String updateJson = String.format("""
                {
                    "id": %d,
                    "firstName": "Avani",
                    "lastName": "Gupta",
                    "email": "avani.senior@luv2code.com"
                }
                """, saved.getId());

        mockMvc.perform(put("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("avani.senior@luv2code.com")));

        Employee reloaded = employeeService.findById(saved.getId());
        assertThat(reloaded.getEmail()).isEqualTo("avani.senior@luv2code.com");
    }

    @Test
    public void testPatchEmployee() throws Exception {
        Employee saved = employeeService.save(new Employee("Carlos", "Santana", "carlos@music.org"));

        String patchJson = """
                {
                    "email": "carlos.legend@music.org"
                }
                """;

        mockMvc.perform(patch("/api/employees/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Carlos")))
                .andExpect(jsonPath("$.email", is("carlos.legend@music.org")));
    }

    @Test
    public void testDeleteEmployeeSuccess() throws Exception {
        Employee saved = employeeService.save(new Employee("Linus", "Torvalds", "linus@kernel.org"));

        mockMvc.perform(delete("/api/employees/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", containsString("Deleted employee id - " + saved.getId())));

        assertThat(employeeService.findById(saved.getId())).isNull();
    }

    @Test
    public void testDeleteEmployeeNotFound() throws Exception {
        mockMvc.perform(delete("/api/employees/8888"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Employee id not found - 8888")));
    }
}
