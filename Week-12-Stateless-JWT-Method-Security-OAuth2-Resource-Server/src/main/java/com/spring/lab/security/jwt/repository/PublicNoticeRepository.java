package com.spring.lab.security.jwt.repository;

import com.spring.lab.security.jwt.entity.PublicNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicNoticeRepository extends JpaRepository<PublicNotice, Long> {
    List<PublicNotice> findByNoticBegDtLessThanEqualAndNoticEndDtGreaterThanEqual(LocalDate todayBeg, LocalDate todayEnd);
}
