package com.spring.lab.security.repository;

import com.spring.lab.security.entity.SupportInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportInquiryRepository extends JpaRepository<SupportInquiry, String> {
}
