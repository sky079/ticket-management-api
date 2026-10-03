package com.ticket.ticketmanagement.dto;

public record RagSearchResult(
        String sourceType,
        Long sourceId,
        String title,
        String content
) {}