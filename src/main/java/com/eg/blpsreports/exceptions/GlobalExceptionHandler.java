package com.eg.blpsreports.exceptions;

import com.eg.blpsreports.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ErrorResponse handleCustom(CustomException ex) {
        return new ErrorResponse(ex.getMessage(), Instant.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation failed");
        return new ErrorResponse(message, Instant.now());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleBadJson(HttpMessageNotReadableException ex) {
        return new ErrorResponse("Некорректный формат запроса", Instant.now());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return new ErrorResponse("Метод " + ex.getMethod() + " не поддерживается для этого запроса", Instant.now());
    }


    @ExceptionHandler(Exception.class)
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse("Внутренняя ошибка сервера", Instant.now());
    }
}
