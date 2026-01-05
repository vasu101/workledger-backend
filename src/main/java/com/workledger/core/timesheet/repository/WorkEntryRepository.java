package com.workledger.core.timesheet.repository;

import com.workledger.core.timesheet.domain.WorkEntry;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, Long> {

    Page<WorkEntry> findByWorkDateBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    Page<WorkEntry> findByWorkEntryStatus(
            WorkEntryStatus workEntryStatus,
            Pageable pageable
    );

    List<WorkEntry> findByWorkDate(
            LocalDate workDate
    );

    @Query("""
            SELECT SUM(w.hoursSpent)
            FROM WorkEntry w
            WHERE w.workDate BETWEEN :startDate AND :endDate
            """)
    Double sumHoursByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
