package com.spring.lab.repository;

import com.spring.lab.repository.dao.EmployeeRepository;
import com.spring.lab.repository.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RepositoryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        // Seed initial deterministic test dataset
        employeeRepository.save(new Employee("Leslie", "Andrews", "leslie@luv2code.com"));
        employeeRepository.save(new Employee("Emma", "Baumgarten", "emma@luv2code.com"));
        employeeRepository.save(new Employee("Avani", "Gupta", "avani@luv2code.com"));
        employeeRepository.save(new Employee("Yuri", "Petrov", "yuri@luv2code.com"));
        employeeRepository.save(new Employee("Juan", "Vega", "juan@luv2code.com"));
    }

    @Test
    @DisplayName("Context Loads & EmployeeRepository Proxy Injected Successfully")
    void contextLoads() {
        assertThat(employeeRepository).isNotNull();
        assertThat(employeeRepository.count()).isEqualTo(5);
    }

    @Test
    @DisplayName("Derived Query: findByLastName returns exact match")
    void testDerivedQueryFindByLastName() {
        List<Employee> results = employeeRepository.findByLastName("Baumgarten");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Emma");
        assertThat(results.get(0).getEmail()).isEqualTo("emma@luv2code.com");
    }

    @Test
    @DisplayName("Derived Query: findByEmailContaining with Pageable returns paged match")
    void testDerivedQueryFindByEmailContaining() {
        Page<Employee> page = employeeRepository.findByEmailContaining("luv2code", PageRequest.of(0, 3));
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Spring Data REST: GET /api/members returns HAL JSON collection with metadata")
    void testSpringDataRestGetMembers() throws Exception {
        mockMvc.perform(get("/api/members")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.members", hasSize(5)))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/members")))
                .andExpect(jsonPath("$.page.totalElements", is(5)))
                .andExpect(jsonPath("$.page.size", is(20)))
                .andExpect(jsonPath("$.page.number", is(0)));
    }

    @Test
    @DisplayName("Spring Data REST: Sorting via ?sort=lastName,desc orders records alphabetically")
    void testSpringDataRestSorting() throws Exception {
        mockMvc.perform(get("/api/members?sort=lastName,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.members[0].lastName", is("Vega")))
                .andExpect(jsonPath("$._embedded.members[1].lastName", is("Petrov")))
                .andExpect(jsonPath("$._embedded.members[4].lastName", is("Andrews")));
    }

    @Test
    @DisplayName("Spring Data REST: Pagination via ?page=0&size=2 returns paginated slice and next link")
    void testSpringDataRestPagination() throws Exception {
        mockMvc.perform(get("/api/members?page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.members", hasSize(2)))
                .andExpect(jsonPath("$.page.size", is(2)))
                .andExpect(jsonPath("$.page.totalElements", is(5)))
                .andExpect(jsonPath("$.page.totalPages", is(3)))
                .andExpect(jsonPath("$._links.next.href", notNullValue()))
                .andExpect(jsonPath("$._links.first.href", notNullValue()))
                .andExpect(jsonPath("$._links.last.href", notNullValue()));
    }

    @Test
    @DisplayName("Spring Data REST: GET /api/members/{id} returns single entity hypermedia resource")
    void testSpringDataRestGetSingleMember() throws Exception {
        List<Employee> employees = employeeRepository.findByLastName("Gupta");
        Integer id = employees.get(0).getId();

        mockMvc.perform(get("/api/members/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Avani")))
                .andExpect(jsonPath("$.lastName", is("Gupta")))
                .andExpect(jsonPath("$.email", is("avani@luv2code.com")))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/members/" + id)));
    }

    @Test
    @DisplayName("Spring Data REST: POST /api/members creates new employee resource")
    void testSpringDataRestPostMember() throws Exception {
        String newEmployeeJson = """
                {
                    "firstName": "Linus",
                    "lastName": "Torvalds",
                    "email": "linus@kernel.org"
                }
                """;

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Linus")))
                .andExpect(jsonPath("$.lastName", is("Torvalds")))
                .andExpect(jsonPath("$.email", is("linus@kernel.org")))
                .andExpect(jsonPath("$._links.self.href", notNullValue()));

        assertThat(employeeRepository.count()).isEqualTo(6);
    }

    @Test
    @DisplayName("Spring Data REST: PATCH /api/members/{id} partially updates employee")
    void testSpringDataRestPatchMember() throws Exception {
        List<Employee> employees = employeeRepository.findByLastName("Andrews");
        Integer id = employees.get(0).getId();

        String patchJson = """
                {
                    "email": "leslie.updated@luv2code.com"
                }
                """;

        mockMvc.perform(patch("/api/members/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Leslie")))
                .andExpect(jsonPath("$.email", is("leslie.updated@luv2code.com")));

        Employee updated = employeeRepository.findById(id).orElseThrow();
        assertThat(updated.getEmail()).isEqualTo("leslie.updated@luv2code.com");
    }

    @Test
    @DisplayName("Spring Data REST: DELETE /api/members/{id} removes resource with 204 No Content")
    void testSpringDataRestDeleteMember() throws Exception {
        List<Employee> employees = employeeRepository.findByLastName("Petrov");
        Integer id = employees.get(0).getId();

        mockMvc.perform(delete("/api/members/" + id))
                .andExpect(status().isNoContent());

        assertThat(employeeRepository.findById(id)).isEmpty();
        assertThat(employeeRepository.count()).isEqualTo(4);
    }

    @Test
    @DisplayName("Spring Data REST: Search endpoint /api/members/search/by-last-name executes derived query")
    void testSpringDataRestSearchEndpoint() throws Exception {
        mockMvc.perform(get("/api/members/search/by-last-name?lastName=Vega")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.members", hasSize(1)))
                .andExpect(jsonPath("$._embedded.members[0].firstName", is("Juan")))
                .andExpect(jsonPath("$._embedded.members[0].email", is("juan@luv2code.com")));
    }
}
