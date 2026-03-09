package com.ticket.ticketmanagement.repository;

// imports
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket , Long> {
    List<Ticket> findByStatus(Ticket.Status status);

    List<Ticket> findByAssignedTo(User user);

    List<Ticket> findByCreatedBy(User user);

    List<Ticket> findBySlaStatus(Ticket.SlaStatus slaStatus);

    long countByStatus(Ticket.Status status);
    long countBySlaStatus(Ticket.SlaStatus slaStatus);

}
