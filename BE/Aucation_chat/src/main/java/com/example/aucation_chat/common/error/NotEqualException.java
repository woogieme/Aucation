package com.example.aucation_chat.common.error;

public class NotEqualException extends ApplicationException {

    public NotEqualException(ApplicationError error) {
        super(error);
    }
}
