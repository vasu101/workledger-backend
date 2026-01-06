package com.workledger.core.timesheet.controller;

import com.workledger.core.common.dto.ApiResponse;
import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.WorkEntryResponse;
import com.workledger.core.timesheet.dto.WorkEntrySummary;
import com.workledger.core.timesheet.service.WorkEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkEntryControllerTest {

    private RestTestClient client;
    private WorkEntryService workEntryService;

    @BeforeEach
    void setUp() {
        workEntryService = Mockito.mock(WorkEntryService.class);
        client = RestTestClient.bindToController(new WorkEntryController(workEntryService)).build();
    }

    @Test
    void createWorkEntry() {
        CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-1",
                "TICKET-1",
                "Test work",
                8.0,
                null
        );

        WorkEntryResponse response = new WorkEntryResponse(
                1L,
                request.workDate(),
                request.programType(),
                request.programReference(),
                request.ticketId(),
                request.description(),
                request.hoursSpent(),
                WorkEntryStatus.DRAFT,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Mockito.when(workEntryService.createWorkEntry(request)).thenReturn(response);

        client.post()
                .uri("/api/v1/work-entries")
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertNotNull(apiResponse);
                    assertTrue(apiResponse.isSuccess());
                });
    }

    @Test
    void getAllWorkEntries() {
        WorkEntrySummary summary1 = new WorkEntrySummary(
                1L,
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-1",
                8.0,
                WorkEntryStatus.DRAFT
        );

        WorkEntrySummary summary2 = new WorkEntrySummary(
                2L,
                LocalDate.now().minusDays(2),
                ProgramType.INTERNAL,
                "PROJ-2",
                6.5,
                WorkEntryStatus.SUBMITTED
        );

        Page<WorkEntrySummary> page = new PageImpl<>(
                List.of(summary1, summary2),
                PageRequest.of(0, 2, Sort.Direction.DESC, "workDate"),
                2
        );

        Mockito.when(workEntryService.getAllWorkEntries(Mockito.any(Pageable.class))).thenReturn(page);

        client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/work-entries")
                        .queryParam("page", 0)
                        .queryParam("size", 2)
                        .queryParam("sortBy", "woorkDate")
                        .queryParam("direction", "DESC")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(apiResponse -> {
                    assertNotNull(apiResponse);
                    assertTrue(apiResponse.isSuccess());
                });

        Mockito.verify(workEntryService).getAllWorkEntries(Mockito.any(Pageable.class));
    }

    @Test
    void returnBadRequestForInvalidInput() {
        CreateWorkEntryRequest invalidRequest = new CreateWorkEntryRequest(
                LocalDate.now().plusDays(1),
                ProgramType.CLIENT,
                "PROJ-1",
                null,
                "Invalid",
                25.0,
                null
        );

        client.post()
                .uri("/api/v1/work-entries")
                .body(invalidRequest)
                .exchange()
                .expectStatus().isBadRequest();
    }
}