package com.example.aucation_chat.common.error;

public class WrongFormException extends ApplicationException {

    public WrongFormException(ApplicationError error) {
        super(error);
    }
}
