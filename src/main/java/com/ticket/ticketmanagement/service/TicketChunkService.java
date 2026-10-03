package com.ticket.ticketmanagement.service;

//import com.ticket.ticketmanagement.ticket.Ticket;
import com.ticket.ticketmanagement.entity.Ticket;
import com.ticket.ticketmanagement.entity.TicketChunk;
import com.ticket.ticketmanagement.dto.TicketSearchResult;
import com.ticket.ticketmanagement.repository.TicketChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketChunkService {

    private final TicketChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public TicketChunkService(
            TicketChunkRepository chunkRepository,
            EmbeddingService embeddingService) {

        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    public List<TicketChunk> createChunks(Ticket ticket) {

        String content = """
                Title: %s

                Description: %s

                Resolution: %s
                """.formatted(
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getResolution()
        );

        int chunkSize = 500;

        List<TicketChunk> chunks = new ArrayList<>();

        for (int start = 0, index = 0;
             start < content.length();
             start += chunkSize, index++) {

            int end = Math.min(start + chunkSize, content.length());

            TicketChunk chunk = new TicketChunk();

            chunk.setTicket(ticket);
            chunk.setContent(content.substring(start, end));
            chunk.setChunkIndex(index);

            float[] embedding =
                    embeddingService.generateEmbedding(chunk.getContent());

            chunk.setEmbedding(embedding);

            chunks.add(chunk);
        }

        return chunkRepository.saveAll(chunks);
    }
    public List<TicketSearchResult> searchSimilar(String question, int limit) {

        float[] questionEmbedding =
                embeddingService.generateEmbedding(question);

        List<TicketChunk> chunks =
                chunkRepository.findSimilarChunks(
                        questionEmbedding,
                        limit
                );

        return chunks.stream()
                .map(chunk -> new TicketSearchResult(
                        chunk.getId(),
                        chunk.getTicket().getId(),
                        chunk.getTicket().getTitle(),
                        chunk.getContent()
                ))
                .toList();
    }
}