package com.ticket.ticketmanagement.repository;

import com.ticket.ticketmanagement.entity.TicketChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketChunkRepository
        extends JpaRepository<TicketChunk, Long> {

    @Query(value = """
            SELECT *
            FROM ticket_chunks
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<TicketChunk> findSimilarChunks(
            @Param("embedding") float[] embedding,
            @Param("limit") int limit
    );
}