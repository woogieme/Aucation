package com.example.aucation.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApplicationException(ApplicationError error) {
        super(error.getMessage());
        this.status = error.getStatus();
        this.code = error.getCode();
    }
}
