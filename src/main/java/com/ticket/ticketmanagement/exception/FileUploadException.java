package com.ticket.ticketmanagement.exception;

import org.springframework.http.HttpStatus;

public class FileUploadException extends BaseException {

    public FileUploadException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public FileUploadException(String message, HttpStatus status) {
        super(message, status);
    }
}