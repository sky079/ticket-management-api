package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.entity.KnowledgeArticle;
import com.ticket.ticketmanagement.service.LlmService;
import com.ticket.ticketmanagement.dto.SearchResult;
import com.ticket.ticketmanagement.service.EmbeddingService;
import com.ticket.ticketmanagement.service.KnowledgeArticleService;
import com.ticket.ticketmanagement.service.KnowledgeChunkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeArticleController {
    private final KnowledgeChunkService chunkService;
    private final KnowledgeArticleService service;
    private final EmbeddingService embeddingService;
    private final LlmService llmService;

    public KnowledgeArticleController(KnowledgeChunkService chunkService, KnowledgeArticleService service, EmbeddingService embeddingService, LlmService llmService) {
        this.chunkService = chunkService;
        this.service = service;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }

    @PostMapping
    public KnowledgeArticle create(@RequestBody KnowledgeArticle article) {
        return service.create(article);
    }

    @GetMapping
    public List<KnowledgeArticle> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public KnowledgeArticle getById(@PathVariable Long id) {
        return service.getById(id);
    }
    @GetMapping("/embedding-test")
    public float[] embeddingTest(@RequestParam String text) {
        return embeddingService.generateEmbedding(text);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @GetMapping("/search-test")
    public List<SearchResult> searchTest(
            @RequestParam String question) {

        return chunkService.searchSimilar(question, 3);
    }

    @GetMapping("/llm-test")
    public String llmTest(@RequestParam String question) {
        return llmService.generateAnswer(question);
    }
}