package com.spring.lab.security.controller;

import com.spring.lab.security.entity.PublicNotice;
import com.spring.lab.security.repository.PublicNoticeRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeRestController {

    private final PublicNoticeRepository publicNoticeRepository;

    public NoticeRestController(PublicNoticeRepository publicNoticeRepository) {
        this.publicNoticeRepository = publicNoticeRepository;
    }

    @GetMapping
    public ResponseEntity<List<PublicNotice>> getActivePublicNotices() {
        List<PublicNotice> notices = publicNoticeRepository.findByActiveTrue();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(notices);
    }
}
