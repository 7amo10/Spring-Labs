package com.spring.lab.security.jwt.controller;

import com.spring.lab.security.jwt.entity.PublicNotice;
import com.spring.lab.security.jwt.repository.PublicNoticeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeRestController {

    private final PublicNoticeRepository noticeRepository;

    public NoticeRestController(PublicNoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @GetMapping
    public ResponseEntity<List<PublicNotice>> getActivePublicNotices() {
        LocalDate today = LocalDate.now();
        List<PublicNotice> notices = noticeRepository.findByNoticBegDtLessThanEqualAndNoticEndDtGreaterThanEqual(today, today);
        return ResponseEntity.ok(notices);
    }
}
