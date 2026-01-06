package com.workledger.core.timesheet.service;

import com.workledger.core.common.exception.InvalidStateException;
import com.workledger.core.common.exception.ResourceNotFoundException;
import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntry;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.mapper.WorkEntryMapper;
import com.workledger.core.timesheet.repository.WorkEntryRepository;
import com.workledger.core.timesheet.service.impl.WorkEntryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class WorkEntryServiceTest {

    private WorkEntryRepository repository;
    private WorkEntryMapper mapper;
    private WorkEntryServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(WorkEntryRepository.class);
        mapper = Mockito.mock(WorkEntryMapper.class);
        service = new WorkEntryServiceImpl(repository, mapper);
    }

    @Test
    void createWorkEntryWithDefaultDraftStatus() {
        CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.SELF_LEARNING,
                "PROJ-1",
                null,
                "Test work",
                5.0,
                null
        );

        WorkEntry workEntry = new WorkEntry();
        workEntry.setWorkEntryStatus(null);

        Mockito.when(mapper.toEntity(request)).thenReturn(workEntry);
        Mockito.when(repository.save(workEntry)).thenReturn(workEntry);

        service.createWorkEntry(request);

        assertEquals(WorkEntryStatus.DRAFT, workEntry.getWorkEntryStatus());
    }

    @Test
    void submitDraftWorkEntry() {
        WorkEntry workEntry = new WorkEntry();
        workEntry.setId(1L);
        workEntry.setWorkEntryStatus(WorkEntryStatus.DRAFT);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(workEntry));
        Mockito.when(repository.save(workEntry)).thenReturn(workEntry);

        service.submitWorkEntry(1L);

        assertEquals(WorkEntryStatus.SUBMITTED, workEntry.getWorkEntryStatus());
    }

    @Test
    void failToSubmitNonDraftEntry() {
        WorkEntry workEntry = new WorkEntry();
        workEntry.setWorkEntryStatus(WorkEntryStatus.SUBMITTED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(workEntry));

        assertThrows(
                InvalidStateException.class,
                () -> service.submitWorkEntry(1L)
        );
    }

    @Test
    void notDeleteLockedEntry() {
        WorkEntry workEntry = new WorkEntry();
        workEntry.setWorkEntryStatus(WorkEntryStatus.LOCKED);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(workEntry));

        assertThrows(
                InvalidStateException.class,
                () -> service.deleteWorkEntry(1L)
        );
    }

    @Test
    void throwWhenEntryNotFound() {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getWorkEntryById(99L)
        );
    }
}