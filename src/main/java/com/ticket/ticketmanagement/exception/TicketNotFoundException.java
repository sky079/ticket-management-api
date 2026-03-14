package com.ticket.ticketmanagement.exception;

import org.springframework.http.HttpStatus;

public class TicketNotFoundException extends BaseException {

    public TicketNotFoundException(Long id) {
        super("Ticket not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}