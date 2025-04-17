package com.example.globus.exception;

import com.example.globus.dto.error.ErrorField;
import com.example.globus.dto.error.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");

        List<ErrorField> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorField(error.getField(), error.getDefaultMessage()))
                .toList();

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .path(path)
                .message("Validation failed")
                .details(details)
                .build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return buildErrorMessage(ex, request);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        return buildErrorMessage(ex, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        return buildErrorMessage(ex, request);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception ex, WebRequest request) {
        return buildErrorMessage(ex, request);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest webRequest) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(path)
                .build();
    }
}
