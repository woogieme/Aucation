package com.example.aucation.common.error;

public class ExpiredJwtException extends JwtException {

    public ExpiredJwtException(ApplicationError error) {
        super(error);
    }
}
