package com.example.aucation.common.error;

public class WrongFormException extends ApplicationException {

    public WrongFormException(ApplicationError error) {
        super(error);
    }
}
