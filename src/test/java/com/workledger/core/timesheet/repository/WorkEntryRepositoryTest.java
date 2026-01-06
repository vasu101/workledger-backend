package com.workledger.core.timesheet.repository;

import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntry;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WorkEntryRepositoryTest {

    @Autowired
    private WorkEntryRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(createEntry(
                LocalDate.now().minusDays(2),
                WorkEntryStatus.DRAFT,
                4.0
        ));

        repository.save(createEntry(
                LocalDate.now().minusDays(1),
                WorkEntryStatus.SUBMITTED,
                7.5
        ));

        repository.save(createEntry(
                LocalDate.now().minusDays(4),
                WorkEntryStatus.SUBMITTED,
                8.0
        ));
    }

    @Test
    void findEntriesByDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().minusDays(1);

        Page<WorkEntry> page = repository.findByWorkDateBetween(startDate, endDate, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    void findEntriesByStatus() {
        Page<WorkEntry> page = repository.findByWorkEntryStatus(WorkEntryStatus.SUBMITTED, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    void findEntriesByWorkDate() {
        LocalDate date = LocalDate.now().minusDays(1);

        List<WorkEntry> entries = repository.findByWorkDate(date);

        assertEquals(1, entries.size());
    }

    @Test
    void calculateTotalHours() {
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().minusDays(1);

        Double totalHours = repository.sumHoursByDateRange(startDate, endDate);

        assertNotNull(totalHours);
        assertEquals(11.5, totalHours);
    }

    private WorkEntry createEntry(
            LocalDate date, WorkEntryStatus status, Double hours
    ) {
        WorkEntry workEntry = new WorkEntry();
        workEntry.setWorkDate(date);
        workEntry.setProgramType(ProgramType.INTERNAL);
        workEntry.setProgramReference("PROJ");
        workEntry.setHoursSpent(hours);
        workEntry.setWorkEntryStatus(status);
        return workEntry;
    }
}