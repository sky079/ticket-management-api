package com.ticket.ticketmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ticketmanagement.dto.CreateTicketRequest;
import com.ticket.ticketmanagement.dto.PageResponse;
import com.ticket.ticketmanagement.dto.TicketResponse;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.service.TicketService;
import com.ticket.ticketmanagement.service.UserService;
import com.ticket.ticketmanagement.validation.FileValidator;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final FileValidator fileValidator;
    private final ObjectMapper objectMapper;

    public TicketController(
            TicketService ticketService,
            UserService userService,
            FileValidator fileValidator,
            ObjectMapper objectMapper) {

        this.ticketService = ticketService;
        this.userService = userService;
        this.fileValidator = fileValidator;
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> createTicket(
            @RequestPart("ticket") String ticketJson,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws Exception {

        CreateTicketRequest request =
                objectMapper.readValue(ticketJson, CreateTicketRequest.class);

        String attachmentUrl = null;

        Ticket ticket = ticketService.createTicket(
                request,
                attachmentUrl
        );

        return ResponseEntity.ok(toResponse(ticket));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<PageResponse<TicketResponse>> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<Ticket> ticketPage =
                ticketService.getAllTicketsPaginated(page, size);

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
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                toResponse(ticketService.getTicketById(id))
        );
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @RequestParam Long agentId) {

        User agent = userService.findById(agentId);

        return ResponseEntity.ok(
                toResponse(ticketService.assignTicket(id, agent))
        );
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<TicketResponse> resolveTicket(
            @PathVariable Long id,
            @RequestParam String resolution) {

        return ResponseEntity.ok(
                toResponse(
                        ticketService.resolveTicket(id, resolution)
                )
        );
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
                ticket.getAssignedTo() != null
                        ? ticket.getAssignedTo().getName()
                        : "Unassigned",
                ticket.getAttachmentUrl(),
                ticket.getCreatedAt(),
                ticket.getResolvedAt()
        );
    }
}
