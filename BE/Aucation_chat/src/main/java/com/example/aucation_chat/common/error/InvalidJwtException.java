package com.example.aucation_chat.common.error;

public class InvalidJwtException extends JwtException {

    public InvalidJwtException(ApplicationError error) {
        super(error);
    }
}
