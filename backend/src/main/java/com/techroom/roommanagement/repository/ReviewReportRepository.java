package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.ReviewReport;
import com.techroom.roommanagement.model.ReviewReport.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Integer> {
    Page<ReviewReport> findByStatus(ReportStatus status, Pageable pageable);
}