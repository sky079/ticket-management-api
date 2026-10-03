package com.ticket.ticketmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class LlmService {

    private final RestClient restClient;

    public LlmService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateAnswer(String prompt) {

        LlmRequest request =
                new LlmRequest("qwen2.5:3b", prompt, false);

        LlmResponse response = restClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(LlmResponse.class);

        return response.response();
    }

    private record LlmRequest(
            String model,
            String prompt,
            boolean stream
    ) {
    }

    private record LlmResponse(
            String response
    ) {
    }
}