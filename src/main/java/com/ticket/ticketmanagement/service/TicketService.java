package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.dto.CreateTicketRequest;
import com.ticket.ticketmanagement.dto.PageResponse;
import com.ticket.ticketmanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ticket.ticketmanagement.entity.SlaPolicy;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.exception.BaseException;
import com.ticket.ticketmanagement.exception.TicketNotFoundException;
import com.ticket.ticketmanagement.repository.SlaPolicyRepository;
import com.ticket.ticketmanagement.repository.TicketRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final TicketChunkService ticketChunkService;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository,
                         SlaPolicyRepository slaPolicyRepository, TicketChunkService ticketChunkService, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.ticketChunkService = ticketChunkService;
        this.userRepository = userRepository;
    }

    // Create a new ticket
    public Ticket createTicket(
            CreateTicketRequest request,
            String attachmentUrl) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User createdBy = (User) authentication.getPrincipal();

        Ticket ticket = new Ticket();

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());

        ticket.setPriority(
                Ticket.Priority.valueOf(
                        request.getPriority().toUpperCase()
                )
        );

        ticket.setAttachmentUrl(attachmentUrl);
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
    public Ticket resolveTicket(Long ticketId, String resolution) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() == Ticket.Status.RESOLVED) {
            throw new BaseException(
                    "Ticket is already resolved",
                    org.springframework.http.HttpStatus.BAD_REQUEST
            );
        }

        ticket.setResolution(resolution);
        ticket.setResolvedAt(LocalDateTime.now());
        ticket.setStatus(Ticket.Status.RESOLVED);
        ticket.setSlaStatus(calculateSla(ticket));

        Ticket savedTicket = ticketRepository.save(ticket);

        ticketChunkService.createChunks(savedTicket);

        return savedTicket;
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

    public PageResponse<Ticket> getAllTicketsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

        return new PageResponse<>(
                ticketPage.getContent(),
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isLast()
        );
    }
}