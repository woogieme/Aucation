package com.example.aucation_chat.common.error;

public class JwtException extends ApplicationException {

    public JwtException(ApplicationError error) {
        super(error);
    }
}
