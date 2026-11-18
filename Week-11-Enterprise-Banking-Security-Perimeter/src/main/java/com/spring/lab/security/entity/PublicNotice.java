package com.spring.lab.security.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "public_notices")
public class PublicNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "notice_title", nullable = false, length = 150)
    private String noticeTitle;

    @Column(name = "notice_details", nullable = false, length = 1000)
    private String noticeDetails;

    @Column(name = "notice_begin_date", nullable = false)
    private LocalDate noticeBeginDate;

    @Column(name = "notice_end_date")
    private LocalDate noticeEndDate;

    @Column(nullable = false)
    private boolean active = true;

    public PublicNotice() {
    }

    public PublicNotice(String noticeTitle, String noticeDetails, LocalDate noticeBeginDate, LocalDate noticeEndDate, boolean active) {
        this.noticeTitle = noticeTitle;
        this.noticeDetails = noticeDetails;
        this.noticeBeginDate = noticeBeginDate;
        this.noticeEndDate = noticeEndDate;
        this.active = active;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDetails() {
        return noticeDetails;
    }

    public void setNoticeDetails(String noticeDetails) {
        this.noticeDetails = noticeDetails;
    }

    public LocalDate getNoticeBeginDate() {
        return noticeBeginDate;
    }

    public void setNoticeBeginDate(LocalDate noticeBeginDate) {
        this.noticeBeginDate = noticeBeginDate;
    }

    public LocalDate getNoticeEndDate() {
        return noticeEndDate;
    }

    public void setNoticeEndDate(LocalDate noticeEndDate) {
        this.noticeEndDate = noticeEndDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
