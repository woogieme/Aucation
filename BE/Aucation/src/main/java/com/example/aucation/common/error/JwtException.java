package com.example.aucation.common.error;

public class JwtException extends ApplicationException {

    public JwtException(ApplicationError error) {
        super(error);
    }
}
