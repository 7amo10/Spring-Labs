package com.spring.lab.security.provider;

import com.spring.lab.security.entity.BankUser;
import com.spring.lab.security.repository.BankUserRepository;
import com.spring.lab.security.service.BankUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BankUsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final BankUserRepository bankUserRepository;
    private final BankUserDetailsService bankUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public BankUsernamePasswordAuthenticationProvider(BankUserRepository bankUserRepository,
                                                     BankUserDetailsService bankUserDetailsService,
                                                     PasswordEncoder passwordEncoder) {
        this.bankUserRepository = bankUserRepository;
        this.bankUserDetailsService = bankUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials() != null ? authentication.getCredentials().toString() : "";

        BankUser bankUser = bankUserRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid user credentials"));

        // Custom domain rule: check if the account is active
        if (!bankUser.isActive()) {
            throw new DisabledException("Bank account has been deactivated. Please contact branch support.");
        }

        // Verify password hash
        if (passwordEncoder.matches(rawPassword, bankUser.getPassword())) {
            UserDetails userDetails = bankUserDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(username, rawPassword, userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid password credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
