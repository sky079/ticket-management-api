package com.ticket.ticketmanagement.repository;
//imports
import com.ticket.ticketmanagement.entity.SlaPolicy;
import com.ticket.ticketmanagement.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SlaPolicyRepository extends  JpaRepository<SlaPolicy, Long> {
    Optional<SlaPolicy> findByPriority(Ticket.Priority priority);
}
