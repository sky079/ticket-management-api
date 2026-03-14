package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.dto.CreateTicketRequest;
import com.ticket.ticketmanagement.dto.TicketResponse;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.service.TicketService;
import com.ticket.ticketmanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> createTicket(
            @Valid
            @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal User currentUser) {

        Ticket.Priority priority = Ticket.Priority.valueOf(request.getPriority().toUpperCase());
        Ticket ticket = ticketService.createTicket(
                request.getTitle(),
                request.getDescription(),
                priority,
                currentUser
        );
        return ResponseEntity.ok(toResponse(ticket));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(ticketService.getTicketById(id)));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @RequestParam Long agentId) {

        User agent = userService.findById(agentId);
        return ResponseEntity.ok(toResponse(ticketService.assignTicket(id, agent)));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> resolveTicket(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(ticketService.resolveTicket(id)));
    }

    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketService.TicketReport> getReport() {
        return ResponseEntity.ok(ticketService.getReport());
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority().name(),
                ticket.getStatus().name(),
                ticket.getSlaStatus().name(),
                ticket.getCreatedBy().getName(),
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getName() : "Unassigned",
                ticket.getCreatedAt(),
                ticket.getResolvedAt()
        );
    }
}