package com.example.aucation.common.error;

public class NotEqualException extends ApplicationException {

    public NotEqualException(ApplicationError error) {
        super(error);
    }
}
