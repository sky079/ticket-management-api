package com.ticket.ticketmanagement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sla_policies")
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Ticket.Priority priority;

    @Column(name = "max_hours", nullable = false)
    private Integer maxHours;

    // Getters
    public Long getId() { return id; }
    public Ticket.Priority getPriority() { return priority; }
    public Integer getMaxHours() { return maxHours; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPriority(Ticket.Priority priority) { this.priority = priority; }
    public void setMaxHours(Integer maxHours) { this.maxHours = maxHours; }

    // Constructors
    public SlaPolicy() {}

    public SlaPolicy(Long id, Ticket.Priority priority, Integer maxHours) {
        this.id = id;
        this.priority = priority;
        this.maxHours = maxHours;
    }
}