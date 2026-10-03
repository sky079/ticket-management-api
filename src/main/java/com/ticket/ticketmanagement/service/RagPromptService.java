package com.ticket.ticketmanagement.service;

import com.ticket.ticketmanagement.dto.RagSearchResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagPromptService {

    public String buildPrompt(
            String question,
            List<RagSearchResult> results) {

        StringBuilder context = new StringBuilder();

        for (RagSearchResult result : results) {

            context.append("Source Type: ")
                    .append(result.sourceType())
                    .append("\n");

            context.append("Source ID: ")
                    .append(result.sourceId())
                    .append("\n");

            context.append("Title: ")
                    .append(result.title())
                    .append("\n");

            context.append("Content: ")
                    .append(result.content())
                    .append("\n\n");
        }

        return """
                You are a support assistant for a ticket management system.

                Answer the user's question using the knowledge provided below.

                Rules:
                - Use only the provided knowledge.
                - Do not invent information.
                - You may use information from both knowledge articles and previously resolved tickets.
                - If the knowledge does not contain the answer, say that you do not have enough information.
                - Give clear and practical steps.

                Knowledge:
                %s

                User Question:
                %s

                Answer:
                """.formatted(context, question);
    }
}