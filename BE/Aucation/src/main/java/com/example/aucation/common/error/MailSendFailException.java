package com.example.aucation.common.error;

public class MailSendFailException extends ApplicationException {

    public MailSendFailException(ApplicationError applicationError) {
        super(applicationError);
    }
}
