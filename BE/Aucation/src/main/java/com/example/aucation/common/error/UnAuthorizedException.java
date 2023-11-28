package com.example.aucation.common.error;

public class UnAuthorizedException extends ApplicationException {

    public UnAuthorizedException(ApplicationError error) {
        super(error);
    }
}
