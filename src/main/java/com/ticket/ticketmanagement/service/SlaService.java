package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.config.SlaProperties;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaService {

    private final TicketRepository ticketRepository;
    private final SlaProperties slaProperties;

    public SlaService(TicketRepository ticketRepository,
                      SlaProperties slaProperties) {
        this.ticketRepository = ticketRepository;
        this.slaProperties = slaProperties;
    }

    public void checkSlaBreaches() {

        List<Ticket> openTickets =
                ticketRepository.findByStatus(Ticket.Status.OPEN);

        for (Ticket ticket : openTickets) {

            LocalDateTime deadline =
                    ticket.getCreatedAt()
                            .plusHours(getSlaHours(ticket.getPriority()));

            if (ticket.getSlaStatus() != Ticket.SlaStatus.BREACHED
                    && LocalDateTime.now().isAfter(deadline)) {

                ticket.setSlaStatus(Ticket.SlaStatus.BREACHED);
            }
        }

        ticketRepository.saveAll(openTickets);
    }

    private long getSlaHours(Ticket.Priority priority) {

        switch (priority) {

            case LOW:
                return slaProperties.getLow();

            case MEDIUM:
                return slaProperties.getMedium();

            case HIGH:
                return slaProperties.getHigh();

            default:
                throw new IllegalArgumentException("Unknown Priority");
        }
    }
}