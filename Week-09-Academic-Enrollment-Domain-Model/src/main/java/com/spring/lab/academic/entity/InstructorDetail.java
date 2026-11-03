package com.spring.lab.academic.entity;

import jakarta.persistence.*;

/**
 * 1:1 Bidirectional Child Entity representing specialized instructor credentials.
 * Omits CascadeType.REMOVE to safeguard the parent Instructor when details are removed.
 */
@Entity
@Table(name = "instructor_detail")
public class InstructorDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "specialization")
    private String specialization;

    @OneToOne(mappedBy = "instructorDetail",
              cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                         CascadeType.DETACH, CascadeType.REFRESH})
    private Instructor instructor;

    public InstructorDetail() {
    }

    public InstructorDetail(String portfolioUrl, String specialization) {
        this.portfolioUrl = portfolioUrl;
        this.specialization = specialization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "InstructorDetail{" +
                "id=" + id +
                ", portfolioUrl='" + portfolioUrl + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }
}
