package com.ticket.ticketmanagement;

import com.ticket.ticketmanagement.entity.SlaPolicy;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import com.ticket.ticketmanagement.exception.BaseException;
import com.ticket.ticketmanagement.exception.TicketNotFoundException;
import com.ticket.ticketmanagement.repository.SlaPolicyRepository;
import com.ticket.ticketmanagement.repository.TicketRepository;
import com.ticket.ticketmanagement.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SlaPolicyRepository slaPolicyRepository;

    @InjectMocks
    private TicketService ticketService;

    private User testUser;
    private Ticket testTicket;
    private SlaPolicy highSlaPolicy;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Sumit");
        testUser.setEmail("sumit@gmail.com");
        testUser.setRole(User.Role.ADMIN);

        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setTitle("Database server is down");
        testTicket.setDescription("Production DB not responding");
        testTicket.setPriority(Ticket.Priority.HIGH);
        testTicket.setStatus(Ticket.Status.OPEN);
        testTicket.setSlaStatus(Ticket.SlaStatus.PENDING);
        testTicket.setCreatedBy(testUser);
        testTicket.setCreatedAt(LocalDateTime.now());

        highSlaPolicy = new SlaPolicy();
        highSlaPolicy.setPriority(Ticket.Priority.HIGH);
        highSlaPolicy.setMaxHours(4);
    }

    @Test
    void createTicket_ShouldCreateTicket_WithCorrectFields() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.createTicket(
                "Database server is down",
                "Production DB not responding",
                Ticket.Priority.HIGH,
                testUser
        );

        assertNotNull(result);
        assertEquals("Database server is down", result.getTitle());
        assertEquals(Ticket.Priority.HIGH, result.getPriority());
        assertEquals(testUser, result.getCreatedBy());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void resolveTicket_ShouldMarkSLAMet_WhenResolvedWithinTime() {
        testTicket.setCreatedAt(LocalDateTime.now().minusHours(2));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(slaPolicyRepository.findByPriority(Ticket.Priority.HIGH))
                .thenReturn(Optional.of(highSlaPolicy));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.resolveTicket(1L);

        assertEquals(Ticket.Status.RESOLVED, result.getStatus());
        assertEquals(Ticket.SlaStatus.MET, result.getSlaStatus());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    void resolveTicket_ShouldMarkSLABreached_WhenResolvedAfterDeadline() {
        testTicket.setCreatedAt(LocalDateTime.now().minusHours(6));
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(slaPolicyRepository.findByPriority(Ticket.Priority.HIGH))
                .thenReturn(Optional.of(highSlaPolicy));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.resolveTicket(1L);

        assertEquals(Ticket.Status.RESOLVED, result.getStatus());
        assertEquals(Ticket.SlaStatus.BREACHED, result.getSlaStatus());
    }

    @Test
    void resolveTicket_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () ->
                ticketService.resolveTicket(99L));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void assignTicket_ShouldThrowException_WhenTicketAlreadyResolved() {
        testTicket.setStatus(Ticket.Status.RESOLVED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        assertThrows(BaseException.class, () ->
                ticketService.assignTicket(1L, testUser));

        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void assignTicket_ShouldSetStatusInProgress_WhenAssigned() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        Ticket result = ticketService.assignTicket(1L, testUser);

        assertEquals(Ticket.Status.IN_PROGRESS, result.getStatus());
        assertEquals(testUser, result.getAssignedTo());
    }
}
