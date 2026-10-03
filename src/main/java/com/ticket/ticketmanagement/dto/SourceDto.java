package com.ticket.ticketmanagement.dto;

public record SourceDto(
        String sourceType,
        Long sourceId,
        String title
) {}