package com.example.aucation.common.error;

public class WrongAccessException extends ApplicationException {

    public WrongAccessException(ApplicationError error) {
        super(error);
    }
}
