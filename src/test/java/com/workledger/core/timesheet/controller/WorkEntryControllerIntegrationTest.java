package com.workledger.core.timesheet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.UpdateWorkEntryRequest;
import com.workledger.core.timesheet.repository.WorkEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Modern Integration tests for WorkEntryController using WebTestClient.
 * Compatible with Spring Boot 4.0.1+
 *
 * WebTestClient provides a fluent API for testing with better readability
 * and more powerful assertions compared to TestRestTemplate.
 *
 * To use WebTestClient, add this dependency:
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-webflux</artifactId>
 *     <scope>test</scope>
 * </dependency>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class WorkEntryControllerWebTestClientTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WorkEntryRepository workEntryRepository;

    private static final String BASE_URL = "/api/v1/work-entries";

    @BeforeEach
    void setUp() {
        workEntryRepository.deleteAll();
    }

    @Test
    void shouldCreateWorkEntry() {
        // Arrange
        CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-001",
                "TICKET-123",
                "Implemented feature X",
                8.0,
                null
        );

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Work entry created successfully")
                .jsonPath("$.data.id").exists()
                .jsonPath("$.data.workDate").isEqualTo(request.workDate().toString())
                .jsonPath("$.data.programType").isEqualTo("CLIENT")
                .jsonPath("$.data.hoursSpent").isEqualTo(8.0)
                .jsonPath("$.data.status").isEqualTo("DRAFT");
    }

    @Test
    void shouldReturnBadRequestForInvalidWorkEntry() {
        // Arrange
        CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                LocalDate.now().plusDays(1), // Future date - invalid
                ProgramType.CLIENT,
                "PROJ-001",
                "TICKET-123",
                "Test",
                25.0, // More than 24 hours - invalid
                null
        );

        // Act & Assert
        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    void shouldGetWorkEntryById() {
        // Arrange - Create a work entry first
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.INTERNAL,
                "PROJ-002",
                null,
                "Internal work",
                6.0,
                null
        );

        String responseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(responseBody);

        // Act & Assert
        webTestClient.get()
                .uri(BASE_URL + "/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(id)
                .jsonPath("$.data.programType").isEqualTo("INTERNAL");
    }

    @Test
    void shouldUpdateWorkEntry() {
        // Arrange - Create a work entry
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-003",
                "TICKET-456",
                "Original description",
                5.0,
                null
        );

        String createResponseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(createResponseBody);

        // Act - Update the work entry
        UpdateWorkEntryRequest updateRequest = new UpdateWorkEntryRequest(
                null,
                null,
                null,
                null,
                "Updated description",
                7.0,
                null
        );

        // Assert
        webTestClient.put()
                .uri(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.description").isEqualTo("Updated description")
                .jsonPath("$.data.hoursSpent").isEqualTo(7.0);
    }

    @Test
    void shouldSubmitWorkEntry() {
        // Arrange - Create a draft work entry
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-004",
                null,
                "Test submission",
                8.0,
                null
        );

        String createResponseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(createResponseBody);

        // Act & Assert - Submit the work entry
        webTestClient.patch()
                .uri(BASE_URL + "/{id}/submit", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.status").isEqualTo("SUBMITTED");
    }

    @Test
    void shouldLockWorkEntry() {
        // Arrange - Create a submitted work entry
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-005",
                null,
                "Test locking",
                8.0,
                WorkEntryStatus.SUBMITTED
        );

        String createResponseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(createResponseBody);

        // Act & Assert - Lock the work entry
        webTestClient.patch()
                .uri(BASE_URL + "/{id}/lock", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.status").isEqualTo("LOCKED");
    }

    @Test
    void shouldDeleteWorkEntry() {
        // Arrange - Create a work entry
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-DEL",
                null,
                "To be deleted",
                8.0,
                null
        );

        String createResponseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(createResponseBody);

        // Act - Delete the work entry
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true);

        // Assert - Verify it's deleted
        webTestClient.get()
                .uri(BASE_URL + "/{id}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldNotDeleteLockedWorkEntry() {
        // Arrange - Create a locked work entry
        CreateWorkEntryRequest createRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-LOCKED",
                null,
                "Locked entry",
                8.0,
                WorkEntryStatus.LOCKED
        );

        String createResponseBody = webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        Long id = extractIdFromResponse(createResponseBody);

        // Act & Assert - Attempt to delete
        webTestClient.delete()
                .uri(BASE_URL + "/{id}", id)
                .exchange()
                .expectStatus().isEqualTo(409); // Conflict
    }

    @Test
    void shouldCalculateTotalHours() {
        // Arrange - Create multiple work entries
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now();

        CreateWorkEntryRequest request1 = new CreateWorkEntryRequest(
                startDate,
                ProgramType.CLIENT,
                "PROJ-A",
                null,
                "Entry 1",
                6.0,
                null
        );

        CreateWorkEntryRequest request2 = new CreateWorkEntryRequest(
                startDate.plusDays(1),
                ProgramType.CLIENT,
                "PROJ-B",
                null,
                "Entry 2",
                7.5,
                null
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request1)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus().isCreated();

        // Act & Assert - Calculate total hours
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/hours/total")
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isEqualTo(13.5);
    }

    @Test
    void shouldGetWorkEntriesByDateRange() {
        // Arrange - Create multiple work entries
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        for (int i = 0; i < 3; i++) {
            CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                    startDate.plusDays(i),
                    ProgramType.CLIENT,
                    "PROJ-" + i,
                    null,
                    "Entry " + i,
                    8.0,
                    null
            );

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated();
        }

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/date-range")
                        .queryParam("startDate", startDate.toString())
                        .queryParam("endDate", endDate.toString())
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.content").isArray()
                .jsonPath("$.data.content.length()").value(count -> {
                    Integer size = (Integer) count;
                    assert size >= 3 : "Expected at least 3 entries";
                });
    }

    @Test
    void shouldGetWorkEntriesByStatus() {
        // Arrange - Create work entries with different statuses
        CreateWorkEntryRequest draftRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-DRAFT",
                null,
                "Draft entry",
                8.0,
                null
        );

        CreateWorkEntryRequest submittedRequest = new CreateWorkEntryRequest(
                LocalDate.now().minusDays(1),
                ProgramType.CLIENT,
                "PROJ-SUBMITTED",
                null,
                "Submitted entry",
                7.0,
                WorkEntryStatus.SUBMITTED
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(draftRequest)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(submittedRequest)
                .exchange()
                .expectStatus().isCreated();

        // Act & Assert - Get entries by DRAFT status
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL + "/status/DRAFT")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.content[0].status").isEqualTo("DRAFT");
    }

    @Test
    void shouldGetWorkEntriesBySpecificDate() {
        // Arrange
        LocalDate testDate = LocalDate.now().minusDays(1);

        CreateWorkEntryRequest request1 = new CreateWorkEntryRequest(
                testDate,
                ProgramType.CLIENT,
                "PROJ-DATE-1",
                null,
                "Entry 1 for date",
                4.0,
                null
        );

        CreateWorkEntryRequest request2 = new CreateWorkEntryRequest(
                testDate,
                ProgramType.INTERNAL,
                "PROJ-DATE-2",
                null,
                "Entry 2 for date",
                4.0,
                null
        );

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request1)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus().isCreated();

        // Act & Assert
        webTestClient.get()
                .uri(BASE_URL + "/date/{date}", testDate)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2);
    }

    @Test
    void shouldGetAllWorkEntriesWithPagination() {
        // Arrange - Create multiple entries
        for (int i = 0; i < 5; i++) {
            CreateWorkEntryRequest request = new CreateWorkEntryRequest(
                    LocalDate.now().minusDays(i),
                    ProgramType.CLIENT,
                    "PROJ-PAGE-" + i,
                    null,
                    "Entry " + i,
                    8.0,
                    null
            );

            webTestClient.post()
                    .uri(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .exchange()
                    .expectStatus().isCreated();
        }

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("page", 0)
                        .queryParam("size", 3)
                        .queryParam("sortBy", "workDate")
                        .queryParam("direction", "DESC")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.content.length()").isEqualTo(3)
                .jsonPath("$.data.totalElements").isEqualTo(5)
                .jsonPath("$.data.totalPages").isEqualTo(2)
                .jsonPath("$.data.first").isEqualTo(true)
                .jsonPath("$.data.last").isEqualTo(false)
                .jsonPath("$.data.hasNext").isEqualTo(true);
    }

    // Helper method
    private Long extractIdFromResponse(String responseBody) {
        try {
            return objectMapper.readTree(responseBody)
                    .get("data")
                    .get("id")
                    .asLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract ID from response", e);
        }
    }
}