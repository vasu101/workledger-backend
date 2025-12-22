package com.workledger.core.timesheet.repository;

import com.workledger.core.timesheet.domain.WorkEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkEntryRepository extends JpaRepository<WorkEntry, Long> {
}
