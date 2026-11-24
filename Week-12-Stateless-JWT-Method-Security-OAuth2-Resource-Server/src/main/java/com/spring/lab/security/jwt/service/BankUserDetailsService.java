package com.spring.lab.security.jwt.service;

import com.spring.lab.security.jwt.entity.BankAuthority;
import com.spring.lab.security.jwt.entity.BankUser;
import com.spring.lab.security.jwt.repository.BankUserRepository;
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

    private final BankUserRepository userRepository;

    public BankUserDetailsService(BankUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BankUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Bank customer not found for email: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
            for (BankAuthority authority : user.getAuthorities()) {
                authorities.add(new SimpleGrantedAuthority(authority.getName()));
            }
        } else if (user.getRole() != null) {
            String roleName = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();
            authorities.add(new SimpleGrantedAuthority(roleName));
        }

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
