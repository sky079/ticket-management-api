package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.entity.KnowledgeArticle;
import com.ticket.ticketmanagement.repository.KnowledgeArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeArticleService {

    private final KnowledgeArticleRepository repository;
    private final KnowledgeChunkService chunkService;

    public KnowledgeArticleService(KnowledgeArticleRepository repository, KnowledgeChunkService chunkService) {
        this.repository = repository;
        this.chunkService = chunkService;
    }

    public KnowledgeArticle create(KnowledgeArticle article) {

        KnowledgeArticle savedArticle = repository.save(article);

        chunkService.createChunks(savedArticle);

        return savedArticle;
    }

    public List<KnowledgeArticle> getAll() {
        return repository.findAll();
    }

    public KnowledgeArticle getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}