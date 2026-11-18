package com.spring.lab.security.controller;

import com.spring.lab.security.dto.BankUserProfileResponse;
import com.spring.lab.security.dto.BankUserRegistrationRequest;
import com.spring.lab.security.entity.BankAuthority;
import com.spring.lab.security.entity.BankUser;
import com.spring.lab.security.exception.CompromisedPasswordException;
import com.spring.lab.security.repository.BankUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class UserAuthenticationRestController {

    private final BankUserRepository bankUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompromisedPasswordChecker compromisedPasswordChecker;

    public UserAuthenticationRestController(BankUserRepository bankUserRepository,
                                          PasswordEncoder passwordEncoder,
                                          CompromisedPasswordChecker compromisedPasswordChecker) {
        this.bankUserRepository = bankUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody BankUserRegistrationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("User email cannot be blank");
        }
        if (bankUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already registered with email: " + request.getEmail());
        }

        // Section 4: Compromised Password Checker (Spring Security 6.3+)
        CompromisedPasswordDecision decision = compromisedPasswordChecker.check(request.getPassword());
        if (decision.isCompromised()) {
            throw new CompromisedPasswordException("Password rejected: The chosen password is vulnerable or appears in breached password dictionaries.");
        }

        // Hashing via delegating password encoder ({bcrypt})
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String role = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole()
                : "ROLE_USER";
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        BankUser bankUser = new BankUser(
                request.getName(),
                request.getEmail(),
                request.getMobileNumber(),
                encodedPassword,
                role
        );

        // Assign standard authorities according to role
        if ("ROLE_ADMIN".equalsIgnoreCase(role)) {
            bankUser.addAuthority("ROLE_ADMIN");
            bankUser.addAuthority("VIEW_ACCOUNT");
            bankUser.addAuthority("VIEW_BALANCE");
            bankUser.addAuthority("VIEW_CARDS");
            bankUser.addAuthority("VIEW_LOANS");
            bankUser.addAuthority("ADMIN_AUDIT");
        } else {
            bankUser.addAuthority("ROLE_USER");
            bankUser.addAuthority("VIEW_ACCOUNT");
            bankUser.addAuthority("VIEW_BALANCE");
            bankUser.addAuthority("VIEW_CARDS");
            bankUser.addAuthority("VIEW_LOANS");
        }

        BankUser saved = bankUserRepository.save(bankUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "SUCCESS",
                "message", "User successfully registered with enterprise security policies",
                "userId", saved.getId(),
                "email", saved.getEmail()
        ));
    }

    @GetMapping("/users/profile")
    public ResponseEntity<BankUserProfileResponse> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        BankUser user = bankUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user details not found: " + email));

        BankUserProfileResponse profile = new BankUserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole(),
                user.isActive(),
                user.getCreatedDate(),
                user.getAuthorities().stream().map(BankAuthority::getName).collect(Collectors.toSet())
        );

        return ResponseEntity.ok(profile);
    }

    @GetMapping("/auth/health")
    public ResponseEntity<Map<String, String>> checkAuthHealth() {
        return ResponseEntity.ok(Map.of("status", "UP", "securityEngine", "ApexBank-Shield"));
    }
}
