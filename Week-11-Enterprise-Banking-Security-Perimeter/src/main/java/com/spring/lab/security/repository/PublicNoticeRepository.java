package com.spring.lab.security.repository;

import com.spring.lab.security.entity.PublicNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicNoticeRepository extends JpaRepository<PublicNotice, Long> {
    List<PublicNotice> findByActiveTrue();
}
