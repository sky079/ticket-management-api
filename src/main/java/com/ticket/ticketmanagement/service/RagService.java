package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.dto.RagSearchResult;
import com.ticket.ticketmanagement.dto.SearchResult;
import com.ticket.ticketmanagement.dto.SourceDto;
import com.ticket.ticketmanagement.dto.TicketSearchResult;
import com.ticket.ticketmanagement.dto.AssistantResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    private final KnowledgeChunkService chunkService;
    private final RagPromptService promptService;
    private final LlmService llmService;
    private final TicketChunkService ticketChunkService;

    public RagService(
            KnowledgeChunkService chunkService,
            RagPromptService promptService,
            LlmService llmService, TicketChunkService ticketChunkService) {

        this.chunkService = chunkService;
        this.promptService = promptService;
        this.llmService = llmService;
        this.ticketChunkService = ticketChunkService;
    }

    public AssistantResponse ask(String question) {

        List<SearchResult> articleResults =
                chunkService.searchSimilar(question, 3);

        List<TicketSearchResult> ticketResults =
                ticketChunkService.searchSimilar(question, 3);

        List<RagSearchResult> results = new ArrayList<>();

        results.addAll(
                articleResults.stream()
                        .map(result -> new RagSearchResult(
                                "KNOWLEDGE_ARTICLE",
                                result.articleId(),
                                result.title(),
                                result.content()
                        ))
                        .toList()
        );

        results.addAll(
                ticketResults.stream()
                        .map(result -> new RagSearchResult(
                                "RESOLVED_TICKET",
                                result.ticketId(),
                                result.title(),
                                result.content()
                        ))
                        .toList()
        );

        String prompt =
                promptService.buildPrompt(question, results);

        String answer =
                llmService.generateAnswer(prompt);

        List<SourceDto> sources = results.stream()
                .map(result -> new SourceDto(
                        result.sourceType(),
                        result.sourceId(),
                        result.title()
                ))
                .distinct()
                .toList();

        return new AssistantResponse(answer, sources);
        }
}