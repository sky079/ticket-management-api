package com.ticket.ticketmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class EmbeddingService {

    private final RestClient restClient;

    public EmbeddingService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public float[] generateEmbedding(String text) {

        EmbeddingRequest request =
                new EmbeddingRequest("nomic-embed-text", text);

        EmbeddingResponse response = restClient.post()
                .uri("/api/embeddings")
                .body(request)
                .retrieve()
                .body(EmbeddingResponse.class);

        List<Float> values = response.embedding();

        float[] result = new float[values.size()];

        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }

        return result;
    }

    private record EmbeddingRequest(
            String model,
            String prompt
    ) {
    }

    private record EmbeddingResponse(
            List<Float> embedding
    ) {
    }
}