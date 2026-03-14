package com.ticket.ticketmanagement.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BaseException {

    public UnauthorizedActionException(String action) {
        super("You are not authorized to: " + action, HttpStatus.FORBIDDEN);
    }
}