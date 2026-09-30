package com.spring.lab.core.controller;

import com.spring.lab.core.common.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/coaches")
public class AthleticDispatcherController {

    // Constructor Injected Dependencies
    private final Coach cricketCoachA;
    private final Coach cricketCoachB;
    private final Coach primaryCoach;
    private final Coach tennisCoach;
    private final Coach baseballCoachA;
    private final Coach baseballCoachB;

    // Setter Injected Dependency
    private Coach swimCoach;

    @Autowired
    public AthleticDispatcherController(
            @Qualifier("cricketCoach") Coach cricketCoachA,
            @Qualifier("cricketCoach") Coach cricketCoachB,
            Coach primaryCoach,
            @Lazy @Qualifier("tennisCoach") Coach tennisCoach,
            @Qualifier("baseballCoach") Coach baseballCoachA,
            @Qualifier("baseballCoach") Coach baseballCoachB) {
        this.cricketCoachA = cricketCoachA;
        this.cricketCoachB = cricketCoachB;
        this.primaryCoach = primaryCoach;
        this.tennisCoach = tennisCoach;
        this.baseballCoachA = baseballCoachA;
        this.baseballCoachB = baseballCoachB;
    }

    /**
     * Demonstrates Setter Injection with @Qualifier for optional/configurable dependencies.
     */
    @Autowired
    public void setSwimCoach(@Qualifier("swimCoach") Coach swimCoach) {
        this.swimCoach = swimCoach;
    }

    @GetMapping("/cricket")
    public Map<String, Object> getCricketWorkout() {
        return buildCoachResponse(cricketCoachA, "Constructor Injection with @Qualifier(\"cricketCoach\")");
    }

    @GetMapping("/primary")
    public Map<String, Object> getPrimaryWorkout() {
        return buildCoachResponse(primaryCoach, "Implicit @Primary Resolution without explicit qualifier");
    }

    @GetMapping("/tennis")
    public Map<String, Object> getTennisWorkout() {
        return buildCoachResponse(tennisCoach, "Lazy-initialized bean triggered on first invocation");
    }

    @GetMapping("/swim")
    public Map<String, Object> getSwimWorkout() {
        return buildCoachResponse(swimCoach, "Adapted 3rd-Party POJO wired via @Configuration + @Bean and Setter Injection");
    }

    @GetMapping("/baseball")
    public Map<String, Object> getBaseballWorkout() {
        return buildCoachResponse(baseballCoachA, "Prototype Scoped Bean (Independent Instance)");
    }

    @GetMapping("/scope-check")
    public Map<String, Object> verifyScopes() {
        boolean singletonMatch = (cricketCoachA == cricketCoachB);
        boolean prototypeMatch = (baseballCoachA == baseballCoachB);

        return Map.of(
            "singletonVerification", Map.of(
                "scope", "SINGLETON",
                "isSameInstance", singletonMatch,
                "instanceA_IdentityHash", System.identityHashCode(cricketCoachA),
                "instanceB_IdentityHash", System.identityHashCode(cricketCoachB),
                "evaluation", singletonMatch ? "PASS: Both references point to identical memory location" : "FAIL"
            ),
            "prototypeVerification", Map.of(
                "scope", "PROTOTYPE",
                "isSameInstance", prototypeMatch,
                "instanceA_IdentityHash", System.identityHashCode(baseballCoachA),
                "instanceB_IdentityHash", System.identityHashCode(baseballCoachB),
                "evaluation", !prototypeMatch ? "PASS: Distinct independent instances allocated per injection point" : "FAIL"
            )
        );
    }

    @GetMapping("/dispatch")
    public Map<String, Object> dispatch(@RequestParam(defaultValue = "primary") String sport) {
        Coach targetCoach = switch (sport.toLowerCase()) {
            case "cricket" -> cricketCoachA;
            case "tennis" -> tennisCoach;
            case "baseball" -> baseballCoachA;
            case "swim" -> swimCoach;
            default -> primaryCoach;
        };
        return buildCoachResponse(targetCoach, "Dynamic Dispatcher Strategy");
    }

    private Map<String, Object> buildCoachResponse(Coach coach, String injectionStyle) {
        return Map.of(
            "coachType", coach.getCoachType(),
            "dailyWorkout", coach.getDailyWorkout(),
            "injectionMechanism", injectionStyle,
            "identityHashCode", System.identityHashCode(coach)
        );
    }
}
