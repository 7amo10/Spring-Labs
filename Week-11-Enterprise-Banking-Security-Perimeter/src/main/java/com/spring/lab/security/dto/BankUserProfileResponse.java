package com.spring.lab.security.dto;

import java.time.LocalDate;
import java.util.Set;

public class BankUserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String role;
    private boolean active;
    private LocalDate createdDate;
    private Set<String> authorities;

    public BankUserProfileResponse() {
    }

    public BankUserProfileResponse(Long id, String name, String email, String mobileNumber, String role,
                                   boolean active, LocalDate createdDate, Set<String> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.active = active;
        this.createdDate = createdDate;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
