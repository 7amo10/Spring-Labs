package com.spring.lab.security.service;

import com.spring.lab.security.entity.BankAuthority;
import com.spring.lab.security.entity.BankUser;
import com.spring.lab.security.repository.BankUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankUserDetailsService implements UserDetailsService {

    private final BankUserRepository bankUserRepository;

    public BankUserDetailsService(BankUserRepository bankUserRepository) {
        this.bankUserRepository = bankUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser user = bankUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // Map primary role (ensure ROLE_ prefix)
        String roleName = user.getRole();
        if (roleName != null && !roleName.isBlank()) {
            if (!roleName.startsWith("ROLE_")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            } else {
                authorities.add(new SimpleGrantedAuthority(roleName));
            }
        }

        // Map individual fine-grained authorities
        if (user.getAuthorities() != null) {
            for (BankAuthority auth : user.getAuthorities()) {
                if (auth.getName() != null && !auth.getName().isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(auth.getName()));
                }
            }
        }

        return new User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
