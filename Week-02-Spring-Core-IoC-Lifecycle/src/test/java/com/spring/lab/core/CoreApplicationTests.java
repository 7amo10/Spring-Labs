package com.spring.lab.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCricketCoachWorkout() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/cricket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Cricket")))
                .andExpect(jsonPath("$.dailyWorkout", containsString("fast bowling")))
                .andExpect(jsonPath("$.injectionMechanism", containsString("@Qualifier(\"cricketCoach\")")));
    }

    @Test
    public void testPrimaryTrackCoachWorkout() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/primary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Track")))
                .andExpect(jsonPath("$.dailyWorkout", containsString("5000-meter")))
                .andExpect(jsonPath("$.injectionMechanism", containsString("@Primary Resolution")));
    }

    @Test
    public void testLazyTennisCoachWorkout() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/tennis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Tennis")))
                .andExpect(jsonPath("$.dailyWorkout", containsString("backhand volleys")))
                .andExpect(jsonPath("$.injectionMechanism", containsString("Lazy-initialized")));
    }

    @Test
    public void testAdaptedSwimCoachWorkout() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/swim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Swim")))
                .andExpect(jsonPath("$.dailyWorkout", containsString("freestyle sprints")))
                .andExpect(jsonPath("$.injectionMechanism", containsString("Adapted 3rd-Party POJO")));
    }

    @Test
    public void testPrototypeBaseballCoachWorkout() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/baseball"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Baseball")))
                .andExpect(jsonPath("$.dailyWorkout", containsString("batting cage")));
    }

    @Test
    public void testScopeVerificationEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/scope-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.singletonVerification.scope", is("SINGLETON")))
                .andExpect(jsonPath("$.singletonVerification.isSameInstance", is(true)))
                .andExpect(jsonPath("$.singletonVerification.evaluation", containsString("PASS")))
                .andExpect(jsonPath("$.prototypeVerification.scope", is("PROTOTYPE")))
                .andExpect(jsonPath("$.prototypeVerification.isSameInstance", is(false)))
                .andExpect(jsonPath("$.prototypeVerification.evaluation", containsString("PASS")));
    }

    @Test
    public void testDynamicDispatch() throws Exception {
        mockMvc.perform(get("/api/v1/coaches/dispatch?sport=swim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Swim")));

        mockMvc.perform(get("/api/v1/coaches/dispatch?sport=cricket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Cricket")));

        mockMvc.perform(get("/api/v1/coaches/dispatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachType", containsString("Track")));
    }
}
