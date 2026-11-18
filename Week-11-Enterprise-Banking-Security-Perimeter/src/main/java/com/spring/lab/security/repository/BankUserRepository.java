package com.spring.lab.security.repository;

import com.spring.lab.security.entity.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankUserRepository extends JpaRepository<BankUser, Long> {
    Optional<BankUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
