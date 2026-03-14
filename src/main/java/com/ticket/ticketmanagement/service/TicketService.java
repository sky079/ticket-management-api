package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.entity.SlaPolicy;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.exception.BaseException;
import com.ticket.ticketmanagement.exception.TicketNotFoundException;
import com.ticket.ticketmanagement.repository.SlaPolicyRepository;
import com.ticket.ticketmanagement.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SlaPolicyRepository slaPolicyRepository;

    public TicketService(TicketRepository ticketRepository,
                         SlaPolicyRepository slaPolicyRepository) {
        this.ticketRepository = ticketRepository;
        this.slaPolicyRepository = slaPolicyRepository;
    }

    // Create a new ticket
    public Ticket createTicket(String title, String description,
                               Ticket.Priority priority, User createdBy) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setCreatedBy(createdBy);
        return ticketRepository.save(ticket);
    }

    // Assign ticket to an agent
    public Ticket assignTicket(Long ticketId, User agent) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == Ticket.Status.RESOLVED) {
            throw new BaseException("Cannot assign a resolved ticket",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        ticket.setAssignedTo(agent);
        ticket.setStatus(Ticket.Status.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    // Resolve a ticket and calculate SLA
    public Ticket resolveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == Ticket.Status.RESOLVED) {
            throw new BaseException("Ticket is already resolved",
                    org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        // Step 1 — mark resolved time
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setStatus(Ticket.Status.RESOLVED);

        // Step 2 — calculate SLA
        ticket.setSlaStatus(calculateSla(ticket));

        return ticketRepository.save(ticket);
    }

    // The SLA engine
    private Ticket.SlaStatus calculateSla(Ticket ticket) {
        SlaPolicy policy = slaPolicyRepository.findByPriority(ticket.getPriority())
                .orElseThrow(() -> new BaseException("No SLA policy found for priority: " + ticket.getPriority(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR));

        long minutesToResolve = Duration.between(ticket.getCreatedAt(),
                ticket.getResolvedAt()).toMinutes();

        long allowedMinutes = policy.getMaxHours() * 60L;

        if (minutesToResolve <= allowedMinutes) {
            return Ticket.SlaStatus.MET;
        } else {
            return Ticket.SlaStatus.BREACHED;
        }

    }

    // Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // Get ticket by id
    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    // Get tickets by status
    public List<Ticket> getTicketsByStatus(Ticket.Status status) {
        return ticketRepository.findByStatus(status);
    }

    // Get tickets assigned to a specific agent
    public List<Ticket> getTicketsByAgent(User agent) {
        return ticketRepository.findByAssignedTo(agent);
    }

    // Report
    public TicketReport getReport() {
        long open = ticketRepository.countByStatus(Ticket.Status.OPEN);
        long inProgress = ticketRepository.countByStatus(Ticket.Status.IN_PROGRESS);
        long resolved = ticketRepository.countByStatus(Ticket.Status.RESOLVED);
        long breached = ticketRepository.countBySlaStatus(Ticket.SlaStatus.BREACHED);

        return new TicketReport(open, inProgress, resolved, breached);
    }

    // Simple report object
    public record TicketReport(long open, long inProgress, long resolved, long breached) {}
}