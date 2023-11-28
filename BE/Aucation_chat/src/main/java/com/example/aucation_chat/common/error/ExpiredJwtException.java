package com.example.aucation_chat.common.error;

public class ExpiredJwtException extends JwtException {

    public ExpiredJwtException(ApplicationError error) {
        super(error);
    }
}
