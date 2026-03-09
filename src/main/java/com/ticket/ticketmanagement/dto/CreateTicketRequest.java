package com.ticket.ticketmanagement.dto;

public class CreateTicketRequest {

    private String title;
    private String description;
    private String priority;

    public CreateTicketRequest() {}

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
}