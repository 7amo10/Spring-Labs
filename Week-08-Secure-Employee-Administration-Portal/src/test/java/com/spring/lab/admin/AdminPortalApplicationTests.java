package com.spring.lab.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class AdminPortalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Unauthenticated request to staff list redirects to custom login page")
    void testUnauthenticatedAccess_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/staff/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Public GET /login displays customized Bootstrap login view")
    void testShowLoginPage_Success() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().string(containsString("Corporate Administration Portal")));
    }

    @Test
    @DisplayName("Valid form login submission authenticates user and redirects to /staff/list")
    void testFormLogin_Success() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .param("username", "john")
                        .param("password", "fun123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/list"));
    }

    @Test
    @DisplayName("Invalid credentials redirect to /login?error")
    void testFormLogin_Failure() throws Exception {
        mockMvc.perform(post("/authenticate")
                        .param("username", "john")
                        .param("password", "invalidpassword")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @DisplayName("EMPLOYEE role can view staff roster list")
    void testEmployeeRole_CanViewStaffList() throws Exception {
        mockMvc.perform(get("/staff/list").with(user("john").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/staff-list"))
                .andExpect(model().attributeExists("staffList"))
                .andExpect(content().string(containsString("Leslie Andrews")))
                .andExpect(content().string(containsString("View Only")));
    }

    @Test
    @DisplayName("EMPLOYEE role is forbidden (403) from accessing add form")
    void testEmployeeRole_BlockedFromAddForm() throws Exception {
        mockMvc.perform(get("/staff/addForm").with(user("john").roles("EMPLOYEE")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("EMPLOYEE role is forbidden (403) from executing delete")
    void testEmployeeRole_BlockedFromDelete() throws Exception {
        mockMvc.perform(post("/staff/delete")
                        .param("staffId", "1")
                        .with(user("john").roles("EMPLOYEE"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MANAGER role can view add and edit forms")
    void testManagerRole_CanAccessAddAndEditForms() throws Exception {
        mockMvc.perform(get("/staff/addForm").with(user("mary").roles("EMPLOYEE", "MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/staff-form"))
                .andExpect(model().attributeExists("employee"));

        mockMvc.perform(get("/staff/editForm")
                        .param("staffId", "1")
                        .with(user("mary").roles("EMPLOYEE", "MANAGER")))
                .andExpect(status().isOk())
                .andExpect(view().name("staff/staff-form"))
                .andExpect(model().attributeExists("employee"))
                .andExpect(content().string(containsString("Leslie")));
    }

    @Test
    @DisplayName("MANAGER role can save staff entity with Post-Redirect-Get pattern")
    void testManagerRole_CanSaveStaffWithPRG() throws Exception {
        mockMvc.perform(post("/staff/save")
                        .param("firstName", "David")
                        .param("lastName", "Warner")
                        .param("email", "david.warner@company.org")
                        .param("department", "Finance")
                        .with(user("mary").roles("EMPLOYEE", "MANAGER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/list"));
    }

    @Test
    @DisplayName("MANAGER role is forbidden (403) from deleting staff record")
    void testManagerRole_BlockedFromDelete() throws Exception {
        mockMvc.perform(post("/staff/delete")
                        .param("staffId", "1")
                        .with(user("mary").roles("EMPLOYEE", "MANAGER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN role can delete staff record with Post-Redirect-Get pattern")
    void testAdminRole_CanDeleteStaffWithPRG() throws Exception {
        mockMvc.perform(post("/staff/delete")
                        .param("staffId", "5")
                        .with(user("susan").roles("EMPLOYEE", "MANAGER", "ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/staff/list"));
    }

    @Test
    @DisplayName("CSRF protection blocks state-modifying POST requests missing CSRF token")
    void testCsrfProtection_BlocksMissingToken() throws Exception {
        mockMvc.perform(post("/staff/delete")
                        .param("staffId", "1")
                        .with(user("susan").roles("ADMIN"))) // Missing CSRF token
                .andExpect(status().isForbidden());
    }
}
