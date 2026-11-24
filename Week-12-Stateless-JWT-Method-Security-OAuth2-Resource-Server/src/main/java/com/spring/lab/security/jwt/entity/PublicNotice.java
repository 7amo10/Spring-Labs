package com.spring.lab.security.jwt.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "public_notices")
public class PublicNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(name = "notice_summary", nullable = false, length = 200)
    private String noticeSummary;

    @Column(name = "notice_details", nullable = false, length = 500)
    private String noticeDetails;

    @Column(name = "notic_beg_dt", nullable = false)
    private LocalDate noticBegDt;

    @Column(name = "notic_end_dt", nullable = false)
    private LocalDate noticEndDt;

    @Column(name = "create_dt", nullable = false)
    private LocalDate createDt;

    public PublicNotice() {
    }

    public PublicNotice(String noticeSummary, String noticeDetails, LocalDate noticBegDt, LocalDate noticEndDt, LocalDate createDt) {
        this.noticeSummary = noticeSummary;
        this.noticeDetails = noticeDetails;
        this.noticBegDt = noticBegDt;
        this.noticEndDt = noticEndDt;
        this.createDt = createDt;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeSummary() {
        return noticeSummary;
    }

    public void setNoticeSummary(String noticeSummary) {
        this.noticeSummary = noticeSummary;
    }

    public String getNoticeDetails() {
        return noticeDetails;
    }

    public void setNoticeDetails(String noticeDetails) {
        this.noticeDetails = noticeDetails;
    }

    public LocalDate getNoticBegDt() {
        return noticBegDt;
    }

    public void setNoticBegDt(LocalDate noticBegDt) {
        this.noticBegDt = noticBegDt;
    }

    public LocalDate getNoticEndDt() {
        return noticEndDt;
    }

    public void setNoticEndDt(LocalDate noticEndDt) {
        this.noticEndDt = noticEndDt;
    }

    public LocalDate getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDate createDt) {
        this.createDt = createDt;
    }
}
