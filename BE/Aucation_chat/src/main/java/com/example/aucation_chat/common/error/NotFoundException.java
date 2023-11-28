package com.example.aucation_chat.common.error;

public class NotFoundException extends ApplicationException {

    public NotFoundException(ApplicationError error) {
        super(error);
    }
}
