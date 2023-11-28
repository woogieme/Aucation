package com.example.aucation_chat.common.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.aucation_chat.common.dto.ErrorResponse;
import com.example.aucation_chat.common.error.ApplicationException;
import com.example.aucation_chat.common.dto.ErrorResponse;
import com.example.aucation_chat.common.error.ApplicationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception) {
        log.info("{}: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        return ResponseEntity.status(exception.getStatus()).body(ErrorResponse.from(exception));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.info("{}: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        return ResponseEntity.internalServerError().body(ErrorResponse.create());
    }

    @MessageExceptionHandler(Exception.class)
    protected void handleMessageException(Exception exception) {
        log.info("{}: {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        log.info("여기를 들리나?");

        //return ResponseEntity.internalServerError().body(ErrorResponse.create());
    }
}