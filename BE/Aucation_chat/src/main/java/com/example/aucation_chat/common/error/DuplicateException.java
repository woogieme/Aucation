package com.example.aucation_chat.common.error;

public class DuplicateException extends ApplicationException {

    public DuplicateException(ApplicationError error) {
        super(error);
    }
}
