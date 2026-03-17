package com.axiora.pec.common.exception;

import com.axiora.pec.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        404,
                        "Not Found",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 409 - Conflict (email already exists)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        409,
                        "Conflict",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 422 - Weightage exceeded
    @ExceptionHandler(WeightageExceededException.class)
    public ResponseEntity<ErrorResponse> handleWeightage(
            WeightageExceededException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(
                        422,
                        "Unprocessable Entity",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 400 - Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        400,
                        "Bad Request",
                        message,
                        request.getRequestURI()
                ));
    }

    // 401 - Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuth(
            AuthenticationException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        401,
                        "Unauthorized",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 403 - Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(
            AccessDeniedException ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(
                        403,
                        "Forbidden",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    // 500 - Any other exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        500,
                        "Internal Server Error",
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }
}