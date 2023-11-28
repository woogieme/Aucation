package com.example.aucation_chat.common.error;

public class UnAuthorizedException extends ApplicationException {

    public UnAuthorizedException(ApplicationError error) {
        super(error);
    }
}
