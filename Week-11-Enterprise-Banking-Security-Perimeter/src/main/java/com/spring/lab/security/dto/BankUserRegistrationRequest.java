package com.spring.lab.security.dto;

public class BankUserRegistrationRequest {

    private String name;
    private String email;
    private String mobileNumber;
    private String password;
    private String role; // e.g. "USER" or "ROLE_USER"

    public BankUserRegistrationRequest() {
    }

    public BankUserRegistrationRequest(String name, String email, String mobileNumber, String password, String role) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
