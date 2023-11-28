package com.example.aucation_chat.common.error;

public class FileUploadException extends ApplicationException {

    public FileUploadException(ApplicationError error) {
        super(error);
    }
}
