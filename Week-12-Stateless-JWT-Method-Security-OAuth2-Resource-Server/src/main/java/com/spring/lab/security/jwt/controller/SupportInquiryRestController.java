package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.entity.SupportInquiry;
import com.spring.lab.security.jwt.service.SupportInquiryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiries")
public class SupportInquiryRestController {

    private final SupportInquiryService inquiryService;

    public SupportInquiryRestController(SupportInquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<List<SupportInquiry>> submitInquiries(@RequestBody List<SupportInquiry> inquiries) {
        List<SupportInquiry> saved = inquiryService.submitInquiries(inquiries);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<SupportInquiry>> getInquiries(@RequestParam("email") String email) {
        List<SupportInquiry> inquiries = inquiryService.getInquiries(email);
        return ResponseEntity.ok(inquiries);
    }
}
