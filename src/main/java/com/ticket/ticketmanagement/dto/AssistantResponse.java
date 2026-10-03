package com.ticket.ticketmanagement.dto;

import java.util.List;

public record AssistantResponse(
        String answer,
        List<SourceDto> sources
) {
}