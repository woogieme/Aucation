package com.example.aucation_chat.common.error;

public class MailSendFailException extends ApplicationException {

    public MailSendFailException(ApplicationError applicationError) {
        super(applicationError);
    }
}
