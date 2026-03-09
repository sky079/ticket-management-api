package com.ticket.ticketmanagement.dto;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String slaStatus;
    private String createdBy;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public TicketResponse() {}

    public TicketResponse(Long id, String title, String description, String priority,
                          String status, String slaStatus, String createdBy,
                          String assignedTo, LocalDateTime createdAt, LocalDateTime resolvedAt) {
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

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getSlaStatus() { return slaStatus; }
    public String getCreatedBy() { return createdBy; }
    public String getAssignedTo() { return assignedTo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(String status) { this.status = status; }
    public void setSlaStatus(String slaStatus) { this.slaStatus = slaStatus; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}