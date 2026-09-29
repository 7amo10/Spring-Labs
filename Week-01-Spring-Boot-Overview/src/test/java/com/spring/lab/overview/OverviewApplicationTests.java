package com.spring.lab.overview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OverviewApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRootEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    public void testSystemInfoEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/system-info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coach", is("Coach Carter")))
                .andExpect(jsonPath("$.team", is("Apex Raptors")))
                .andExpect(jsonPath("$.environment", is("production-us-east")))
                .andExpect(jsonPath("$.nodeId", is("node-omega-01")))
                .andExpect(jsonPath("$.status", is("HEALTHY")));
    }

    @Test
    public void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.components.customCluster.details.clusterNode", is("node-omega-01")))
                .andExpect(jsonPath("$.components.customCluster.details.gatewayStatus", is("OPERATIONAL")));
    }

    @Test
    public void testActuatorInfoEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk());
    }
}
