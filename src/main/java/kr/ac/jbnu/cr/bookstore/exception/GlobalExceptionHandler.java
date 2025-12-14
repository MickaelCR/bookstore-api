package kr.ac.jbnu.cr.bookstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import kr.ac.jbnu.cr.bookstore.dto.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MediaType PROBLEM_JSON = MediaType.APPLICATION_JSON;
    private static final String CODE_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    private static final String CODE_DUPLICATE_RESOURCE = "DUPLICATE_RESOURCE";
    private static final String CODE_STATE_CONFLICT = "STATE_CONFLICT";
    private static final String CODE_UNAUTHORIZED = "UNAUTHORIZED";
    private static final String CODE_FORBIDDEN = "FORBIDDEN";
    private static final String CODE_BAD_REQUEST = "BAD_REQUEST";
    private static final String CODE_VALIDATION_FAILED = "VALIDATION_FAILED";
    private static final String CODE_INTERNAL_ERROR = "INTERNAL_SERVER_ERROR";
    private static final String CODE_METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, CODE_RESOURCE_NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, CODE_DUPLICATE_RESOURCE, ex.getMessage(), request);
    }

    @ExceptionHandler(StateConflictException.class)
    public ResponseEntity<ErrorResponse> handleStateConflict(StateConflictException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, CODE_STATE_CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.UNAUTHORIZED, CODE_UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.FORBIDDEN, CODE_FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, CODE_BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        logger.warn("Validation error: {}", fieldErrors);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(CODE_VALIDATION_FAILED)
                .message("Input validation failed")
                .path(request.getRequestURI())
                .details(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, CODE_BAD_REQUEST, "Request body is missing or malformed", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, CODE_METHOD_NOT_ALLOWED,
                "HTTP method " + ex.getMethod() + " is not supported for this endpoint", request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, CODE_RESOURCE_NOT_FOUND,
                "Endpoint " + ex.getRequestURL() + " not found", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, CODE_INTERNAL_ERROR,
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code, String message, HttpServletRequest request) {
        logger.warn("{}: {}", code, message);

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status)
                .contentType(PROBLEM_JSON)
                .body(error);
    }
}