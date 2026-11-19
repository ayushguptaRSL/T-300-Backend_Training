package com.ayush.backendtraining.exception;

import com.ayush.backendtraining.dto.ErrorDTO;
import com.ayush.backendtraining.dto.ValidationExceptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {

        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleRunTimeException(RuntimeException ex, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFound(ResourceNotFound ex, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(InvalidDateRange.class)
    public ResponseEntity<ErrorDTO> handleInvalidDateRange(InvalidDateRange ex, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDTO> handleMethodValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String , String> fieldErrors = new HashMap<>();
        
        ex.getFieldErrors().forEach((fieldError) -> {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ValidationExceptionDTO error = new ValidationExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                fieldErrors,
                request.getRequestURI()
        );
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
