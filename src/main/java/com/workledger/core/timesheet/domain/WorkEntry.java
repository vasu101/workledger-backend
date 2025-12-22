package com.workledger.core.timesheet.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_entries")
public class WorkEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate workDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgramType programType;

    @Column(nullable = false)
    private String programReference;

    private String ticketId;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double hoursSpent;

    @Enumerated(EnumType.STRING)
    private WorkEntryStatus workEntryStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
