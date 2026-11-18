package com.spring.lab.security.controller;

import com.spring.lab.security.entity.SupportInquiry;
import com.spring.lab.security.repository.SupportInquiryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/support")
public class SupportInquiryRestController {

    private final SupportInquiryRepository supportInquiryRepository;

    public SupportInquiryRestController(SupportInquiryRepository supportInquiryRepository) {
        this.supportInquiryRepository = supportInquiryRepository;
    }

    @PostMapping("/inquiries")
    public ResponseEntity<SupportInquiry> submitInquiry(@RequestBody SupportInquiry inquiry) {
        if (inquiry.getCustomerName() == null || inquiry.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (inquiry.getContactEmail() == null || inquiry.getContactEmail().isBlank()) {
            throw new IllegalArgumentException("Contact email cannot be empty");
        }
        SupportInquiry saved = supportInquiryRepository.save(inquiry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
