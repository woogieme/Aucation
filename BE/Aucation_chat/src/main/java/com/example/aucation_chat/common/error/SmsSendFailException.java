package com.example.aucation_chat.common.error;

public class SmsSendFailException extends RuntimeException {

    public SmsSendFailException(ApplicationError error) {
        super(error.getMessage());
    }
}
