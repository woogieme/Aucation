package com.example.aucation_chat.common.error;

public class BadRequestException extends ApplicationException {

    public BadRequestException(ApplicationError error) {
        super(error);
    }
}
