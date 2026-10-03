package com.ticket.ticketmanagement.validation;

import com.ticket.ticketmanagement.exception.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/pdf"
    );

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File cannot be empty");
        }

        if (file.getOriginalFilename() == null
                || file.getOriginalFilename().isBlank()) {

            throw new FileUploadException("Invalid file name");
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new FileUploadException(
                    "File size cannot exceed 10 MB"
            );
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {

            throw new FileUploadException(
                    "Only PNG, JPG, JPEG and PDF files are allowed."
            );
        }
    }
}
