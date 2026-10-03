package com.ticket.ticketmanagement.repository;

import com.ticket.ticketmanagement.entity.KnowledgeChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeChunkRepository
        extends JpaRepository<KnowledgeChunk, Long> {

    @Query(value = """
            SELECT *
            FROM knowledge_chunks
            WHERE embedding IS NOT NULL
            ORDER BY embedding <=> CAST(:embedding AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<KnowledgeChunk> findSimilarChunks(
            @Param("embedding") float[] embedding,
            @Param("limit") int limit
    );
}