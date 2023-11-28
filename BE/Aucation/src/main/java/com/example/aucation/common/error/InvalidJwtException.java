package com.example.aucation.common.error;

public class InvalidJwtException extends JwtException {

    public InvalidJwtException(ApplicationError error) {
        super(error);
    }
}
