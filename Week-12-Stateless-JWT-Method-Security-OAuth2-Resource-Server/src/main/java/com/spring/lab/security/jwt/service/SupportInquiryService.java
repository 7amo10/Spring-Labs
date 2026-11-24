package com.spring.lab.security.jwt.service;

import com.spring.lab.security.jwt.entity.SupportInquiry;
import com.spring.lab.security.jwt.repository.SupportInquiryRepository;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupportInquiryService {

    private final SupportInquiryRepository inquiryRepository;

    public SupportInquiryService(SupportInquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    @PreFilter("filterObject.contactName != 'BlockedSender'")
    public List<SupportInquiry> submitInquiries(List<SupportInquiry> inquiries) {
        List<SupportInquiry> saved = new ArrayList<>();
        for (SupportInquiry item : inquiries) {
            saved.add(inquiryRepository.save(item));
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<SupportInquiry> getInquiries(String contactEmail) {
        return inquiryRepository.findByContactEmail(contactEmail);
    }
}
