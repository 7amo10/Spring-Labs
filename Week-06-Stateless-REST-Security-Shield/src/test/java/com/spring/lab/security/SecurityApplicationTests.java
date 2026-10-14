package com.spring.lab.security;

import com.spring.lab.security.dao.EmployeeRepository;
import com.spring.lab.security.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Integer testEmployeeId;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        // Seed initial employees
        Employee emp1 = employeeRepository.save(new Employee("Leslie", "Andrews", "leslie@luv2code.com"));
        employeeRepository.save(new Employee("Emma", "Baumgarten", "emma@luv2code.com"));
        employeeRepository.save(new Employee("Avani", "Gupta", "avani@luv2code.com"));

        testEmployeeId = emp1.getId();
    }

    @Test
    @DisplayName("Unauthenticated Request to /api/employees returns 401 Unauthorized")
    void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Invalid Credentials returns 401 Unauthorized")
    void testInvalidCredentialsReturns401() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .with(httpBasic("john", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JDBC Auth & BCrypt: john (ROLE_EMPLOYEE) can GET /api/employees with 200 OK")
    void testJohnCanGetEmployees() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .with(httpBasic("john", "fun123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].firstName", is("Leslie")));
    }

    @Test
    @DisplayName("JDBC Auth & BCrypt: john (ROLE_EMPLOYEE) can GET single employee /api/employees/{id}")
    void testJohnCanGetSingleEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/" + testEmployeeId)
                        .with(httpBasic("john", "fun123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Leslie")))
                .andExpect(jsonPath("$.email", is("leslie@luv2code.com")));
    }

    @Test
    @DisplayName("RBAC: john (ROLE_EMPLOYEE) is FORBIDDEN (403) from POST /api/employees")
    void testJohnForbiddenFromPost() throws Exception {
        String newEmp = """
                {"firstName":"Forbidden","lastName":"User","email":"forbidden@luv2code.com"}
                """;

        mockMvc.perform(post("/api/employees")
                        .with(httpBasic("john", "fun123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmp))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC: john (ROLE_EMPLOYEE) is FORBIDDEN (403) from DELETE /api/employees/{id}")
    void testJohnForbiddenFromDelete() throws Exception {
        mockMvc.perform(delete("/api/employees/" + testEmployeeId)
                        .with(httpBasic("john", "fun123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC: mary (ROLE_MANAGER) can POST new employee with 200 OK")
    void testMaryCanPostEmployee() throws Exception {
        String newEmp = """
                {"firstName":"Carlos","lastName":"Santana","email":"carlos@luv2code.com"}
                """;

        mockMvc.perform(post("/api/employees")
                        .with(httpBasic("mary", "fun123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Carlos")))
                .andExpect(jsonPath("$.lastName", is("Santana")));

        assertThat(employeeRepository.count()).isEqualTo(4);
    }

    @Test
    @DisplayName("RBAC: mary (ROLE_MANAGER) can PUT update employee with 200 OK")
    void testMaryCanPutEmployee() throws Exception {
        String updateEmp = String.format("""
                {
                    "id": %d,
                    "firstName": "Leslie-Updated",
                    "lastName": "Andrews",
                    "email": "leslie@luv2code.com"
                }
                """, testEmployeeId);

        mockMvc.perform(put("/api/employees")
                        .with(httpBasic("mary", "fun123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateEmp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Leslie-Updated")));
    }

    @Test
    @DisplayName("RBAC: mary (ROLE_MANAGER) can PATCH employee with 200 OK")
    void testMaryCanPatchEmployee() throws Exception {
        String patchEmp = """
                {"email":"leslie.patched@luv2code.com"}
                """;

        mockMvc.perform(patch("/api/employees/" + testEmployeeId)
                        .with(httpBasic("mary", "fun123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchEmp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("leslie.patched@luv2code.com")));
    }

    @Test
    @DisplayName("RBAC: mary (ROLE_MANAGER) is FORBIDDEN (403) from DELETE /api/employees/{id}")
    void testMaryForbiddenFromDelete() throws Exception {
        mockMvc.perform(delete("/api/employees/" + testEmployeeId)
                        .with(httpBasic("mary", "fun123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RBAC: susan (ROLE_ADMIN) can DELETE /api/employees/{id} with 200 OK")
    void testSusanCanDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/" + testEmployeeId)
                        .with(httpBasic("susan", "fun123")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Deleted employee id - " + testEmployeeId)));

        assertThat(employeeRepository.findById(testEmployeeId)).isEmpty();
        assertThat(employeeRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Admin End-to-End: susan (ROLE_ADMIN) can execute full lifecycle including GET and DELETE")
    void testSusanFullLifecycle() throws Exception {
        mockMvc.perform(get("/api/employees")
                        .with(httpBasic("susan", "fun123")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/employees/" + testEmployeeId)
                        .with(httpBasic("susan", "fun123")))
                .andExpect(status().isOk());
    }
}
