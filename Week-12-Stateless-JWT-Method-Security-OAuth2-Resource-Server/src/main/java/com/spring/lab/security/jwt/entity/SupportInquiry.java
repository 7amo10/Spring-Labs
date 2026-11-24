package com.spring.lab.security.jwt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "support_inquiries")
public class SupportInquiry {

    @Id
    @Column(name = "inquiry_id", nullable = false, length = 50)
    private String inquiryId;

    @Column(name = "contact_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contact_email", nullable = false, length = 120)
    private String contactEmail;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "create_dt", nullable = false)
    private LocalDate createDt;

    public SupportInquiry() {
    }

    public SupportInquiry(String inquiryId, String contactName, String contactEmail, String subject, String message, LocalDate createDt) {
        this.inquiryId = inquiryId;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.subject = subject;
        this.message = message;
        this.createDt = createDt;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public LocalDate getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDate createDt) {
        this.createDt = createDt;
    }
}
