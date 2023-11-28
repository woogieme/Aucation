package com.example.aucation.common.error;

public class NotFoundException extends ApplicationException {

    public NotFoundException(ApplicationError error) {
        super(error);
    }
}
