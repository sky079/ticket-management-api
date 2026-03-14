package com.ticket.ticketmanagement.dto;
import jakarta.validation.constraints.NotBlank;

public class CreateTicketRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Priority cannot be empty")
    private String priority;

    public CreateTicketRequest() {}

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
}