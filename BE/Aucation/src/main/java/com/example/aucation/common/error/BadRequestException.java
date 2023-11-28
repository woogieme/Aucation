package com.example.aucation.common.error;

public class BadRequestException extends ApplicationException {

    public BadRequestException(ApplicationError error) {
        super(error);
    }
}
