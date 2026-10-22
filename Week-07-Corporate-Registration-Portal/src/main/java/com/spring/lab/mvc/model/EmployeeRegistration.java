package com.spring.lab.mvc.model;

import com.spring.lab.mvc.validation.CorporateEmail;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Form-backing model bean representing an employee corporate onboarding submission.
 */
public class EmployeeRegistration {

    private String firstName;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String lastName;

    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    @CorporateEmail(domain = "@company.org", message = "must end with official domain @company.org")
    private String corporateEmail;

    @Pattern(regexp = "^[a-zA-Z0-9]{5}", message = "must be exactly 5 alphanumeric characters")
    private String postalCode;

    @NotNull(message = "is required")
    @Min(value = 30000, message = "must be greater than or equal to 30,000")
    @Max(value = 250000, message = "must be less than or equal to 250,000")
    private Integer salary;

    private String department;

    private String employmentType;

    private List<String> selectedBenefits = new ArrayList<>();

    public EmployeeRegistration() {
    }

    public EmployeeRegistration(String firstName, String lastName, String corporateEmail,
                                String postalCode, Integer salary, String department,
                                String employmentType, List<String> selectedBenefits) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.corporateEmail = corporateEmail;
        this.postalCode = postalCode;
        this.salary = salary;
        this.department = department;
        this.employmentType = employmentType;
        this.selectedBenefits = selectedBenefits;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCorporateEmail() {
        return corporateEmail;
    }

    public void setCorporateEmail(String corporateEmail) {
        this.corporateEmail = corporateEmail;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public List<String> getSelectedBenefits() {
        return selectedBenefits;
    }

    public void setSelectedBenefits(List<String> selectedBenefits) {
        this.selectedBenefits = selectedBenefits;
    }

    @Override
    public String toString() {
        return "EmployeeRegistration{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", corporateEmail='" + corporateEmail + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", salary=" + salary +
                ", department='" + department + '\'' +
                ", employmentType='" + employmentType + '\'' +
                ", selectedBenefits=" + selectedBenefits +
                '}';
    }
}
