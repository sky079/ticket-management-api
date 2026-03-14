package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.dto.CreateTicketRequest;
import com.ticket.ticketmanagement.dto.PageResponse;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket management endpoints")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;

    public TicketController(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }


    @PostMapping
    @Operation(summary = "Create a ticket", description = "Creates a new support ticket")
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
    @Operation(summary = "Get all tickets", description = "Returns paginated list of all tickets")
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<PageResponse<TicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<Ticket> ticketPage = ticketService.getAllTicketsPaginated(page, size);

        List<TicketResponse> ticketResponses = ticketPage.getContent()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PageResponse<TicketResponse> response = new PageResponse<>(
                ticketResponses,
                ticketPage.getPageNumber(),
                ticketPage.getPageSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages(),
                ticketPage.isLastPage()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID", description = "Returns a single ticket by its ID")
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(ticketService.getTicketById(id)));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign ticket", description = "Assigns ticket to an agent - ADMIN only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @RequestParam Long agentId) {

        User agent = userService.findById(agentId);
        return ResponseEntity.ok(toResponse(ticketService.assignTicket(id, agent)));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "Resolve ticket", description = "Resolves ticket and calculates SLA - AGENT and ADMIN only")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> resolveTicket(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(ticketService.resolveTicket(id)));
    }

    @GetMapping("/report")
    @Operation(summary = "Get report", description = "Returns ticket statistics - ADMIN only")
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