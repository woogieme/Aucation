package com.example.aucation.common.error;

public class FileUploadException extends ApplicationException {

    public FileUploadException(ApplicationError error) {
        super(error);
    }
}
