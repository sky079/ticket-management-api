package com.ticket.ticketmanagement.controller;

import com.ticket.ticketmanagement.dto.AssistantResponse;
import com.ticket.ticketmanagement.service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final RagService ragService;

    public AssistantController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask")
    public AssistantResponse ask(@RequestParam String question) {
        return ragService.ask(question);
    }
}