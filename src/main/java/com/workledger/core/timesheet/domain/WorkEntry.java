package com.workledger.core.timesheet.domain;

import com.workledger.core.common.exception.InvalidStateException;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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

    public void canModify() {
        if(this.workEntryStatus == WorkEntryStatus.LOCKED) {
            throw new InvalidStateException(
                    "Cannot modify work entry in LOCKED status",
                    workEntryStatus.name(),
                    "DRAFT or SUBMITTED"
            );
        }
    }

    public void submit() {
        if(workEntryStatus != WorkEntryStatus.DRAFT) {
            throw new InvalidStateException(
                    "Only DRAFT work entries can be submitted",
                    workEntryStatus.name(),
                    WorkEntryStatus.DRAFT.name()
            );
        }
        this.workEntryStatus = WorkEntryStatus.SUBMITTED;
    }

    public void lock() {
        if(workEntryStatus != WorkEntryStatus.SUBMITTED) {
            throw new InvalidStateException(
                    "Only SUBMITTED work entries can be locked",
                    workEntryStatus.name(),
                    WorkEntryStatus.DRAFT.name()
            );
        }
        this.workEntryStatus = WorkEntryStatus.LOCKED;
    }
}
