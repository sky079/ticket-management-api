package com.ticket.ticketmanagement.dto;

public record SearchResult(
        Long chunkId,
        Long articleId,
        String title,
        String category,
        String content
) {
}