package com.example.aucation_chat.common.error;

public class WrongAccessException extends ApplicationException {

    public WrongAccessException(ApplicationError error) {
        super(error);
    }
}
