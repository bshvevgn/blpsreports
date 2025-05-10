package com.eg.blpsreports.controller.handler;

import com.eg.blpsreports.dto.ErrorResponse;
import com.eg.blpsreports.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@Slf4j
@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(HttpMessageNotReadableException ex) {
        loggingException(ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        loggingException(ex);
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        loggingException(ex);
        return new ResponseEntity<>(new ErrorResponse("Такой страницы не существует", Instant.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        loggingException(ex);
        return ResponseEntity.badRequest().body(new ErrorResponse("Невалидное тело запроса", Instant.now()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable ex) {
        loggingException(ex);
        return ResponseEntity.internalServerError().body(new ErrorResponse("Что-то пошло не так...", Instant.now()));
    }

    private void loggingException(Throwable ex) {
        log.error("Something happens...");
        ex.printStackTrace();
    }
}