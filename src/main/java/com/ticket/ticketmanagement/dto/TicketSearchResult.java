package com.ticket.ticketmanagement.dto;

public record TicketSearchResult(
        Long chunkId,
        Long ticketId,
        String title,
        String content
) {}