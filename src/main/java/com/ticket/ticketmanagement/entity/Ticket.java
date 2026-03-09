package com.ticket.ticketmanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "sla_status")
    private SlaStatus slaStatus;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = Status.OPEN;
        this.slaStatus = SlaStatus.PENDING;
    }

    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Status { OPEN, IN_PROGRESS, RESOLVED }
    public enum SlaStatus { PENDING, MET, BREACHED }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public SlaStatus getSlaStatus() { return slaStatus; }
    public User getCreatedBy() { return createdBy; }
    public User getAssignedTo() { return assignedTo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(Status status) { this.status = status; }
    public void setSlaStatus(SlaStatus slaStatus) { this.slaStatus = slaStatus; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    // Constructors
    public Ticket() {}

    public Ticket(Long id, String title, String description, Priority priority,
                  Status status, SlaStatus slaStatus, User createdBy, User assignedTo,
                  LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.slaStatus = slaStatus;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }
}