package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.entity.KnowledgeArticle;
import com.ticket.ticketmanagement.entity.KnowledgeChunk;
import com.ticket.ticketmanagement.dto.SearchResult;
import com.ticket.ticketmanagement.repository.KnowledgeChunkRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeChunkService {

    private final KnowledgeChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public KnowledgeChunkService(KnowledgeChunkRepository chunkRepository,
                                 EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }
    public List<SearchResult> searchSimilar(String question, int limit) {

        float[] questionEmbedding =
                embeddingService.generateEmbedding(question);

        List<KnowledgeChunk> chunks =
                chunkRepository.findSimilarChunks(
                        questionEmbedding,
                        limit
                );

        return chunks.stream()
                .map(chunk -> new SearchResult(
                        chunk.getId(),
                        chunk.getArticle().getId(),
                        chunk.getArticle().getTitle(),
                        chunk.getArticle().getCategory(),
                        chunk.getContent()
                ))
                .toList();
    }

    public List<KnowledgeChunk> createChunks(KnowledgeArticle article) {

        String content = article.getContent();

        int chunkSize = 500;
        List<KnowledgeChunk> chunks = new ArrayList<>();

        for (int start = 0, index = 0;
             start < content.length();
             start += chunkSize, index++) {

            int end = Math.min(start + chunkSize, content.length());

            KnowledgeChunk chunk = new KnowledgeChunk();

            chunk.setArticle(article);
            chunk.setContent(content.substring(start, end));
            chunk.setChunkIndex(index);

            float[] embedding =
                    embeddingService.generateEmbedding(chunk.getContent());

            chunk.setEmbedding(embedding);

            chunks.add(chunk);
        }

        return chunkRepository.saveAll(chunks);
    }
}