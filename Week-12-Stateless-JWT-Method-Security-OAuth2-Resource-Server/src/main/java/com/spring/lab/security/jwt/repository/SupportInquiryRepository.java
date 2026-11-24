package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.SupportInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportInquiryRepository extends JpaRepository<SupportInquiry, String> {
    List<SupportInquiry> findByContactEmail(String contactEmail);
}
