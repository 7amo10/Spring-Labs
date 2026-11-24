package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.dto.BankUserProfileResponse;
import com.spring.lab.security.jwt.dto.BankUserRegistrationRequest;
import com.spring.lab.security.jwt.entity.BankAuthority;
import com.spring.lab.security.jwt.entity.BankUser;
import com.spring.lab.security.jwt.repository.BankUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class UserAuthenticationRestController {

    private final BankUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationRestController(BankUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody BankUserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered: " + request.email());
        }

        String encodedPwd = passwordEncoder.encode(request.password());
        String roleName = request.role() != null ? request.role() : "ROLE_USER";
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName.toUpperCase();
        }

        BankUser newUser = new BankUser(
                request.name(),
                request.email(),
                encodedPwd,
                request.mobileNumber(),
                roleName,
                LocalDate.now()
        );

        newUser.addAuthority(new BankAuthority(roleName, newUser));
        BankUser saved = userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToProfileResponse(saved));
    }

    @GetMapping({"/auth/login", "/user/profile"})
    public ResponseEntity<BankUserProfileResponse> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userRepository.findByEmail(authentication.getName())
                .map(user -> ResponseEntity.ok(mapToProfileResponse(user)))
                .orElseGet(() -> {
                    // For OAuth2 federated tokens where user might not be in local DB
                    List<String> authorities = authentication.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());
                    BankUserProfileResponse response = new BankUserProfileResponse(
                            0L,
                            authentication.getName(),
                            authentication.getName(),
                            "N/A",
                            "FEDERATED",
                            LocalDate.now(),
                            authorities
                    );
                    return ResponseEntity.ok(response);
                });
    }

    private BankUserProfileResponse mapToProfileResponse(BankUser user) {
        List<String> authorities = user.getAuthorities().stream()
                .map(BankAuthority::getName)
                .collect(Collectors.toList());
        return new BankUserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole(),
                user.getCreateDt(),
                authorities
        );
    }
}
