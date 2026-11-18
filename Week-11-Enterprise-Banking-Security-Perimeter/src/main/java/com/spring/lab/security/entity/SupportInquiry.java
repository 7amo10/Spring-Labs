package com.spring.lab.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "support_inquiries")
public class SupportInquiry {

    @Id
    @Column(name = "inquiry_id", length = 64)
    private String inquiryId;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "submitted_date", nullable = false)
    private LocalDate submittedDate = LocalDate.now();

    public SupportInquiry() {
        this.inquiryId = "INQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.submittedDate = LocalDate.now();
    }

    public SupportInquiry(String customerName, String contactEmail, String subject, String message) {
        this();
        this.customerName = customerName;
        this.contactEmail = contactEmail;
        this.subject = subject;
        this.message = message;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
    }
}
