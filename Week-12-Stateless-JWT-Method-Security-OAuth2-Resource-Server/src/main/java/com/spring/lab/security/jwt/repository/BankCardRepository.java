package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
    List<BankCard> findByCustomerEmail(String customerEmail);
}
