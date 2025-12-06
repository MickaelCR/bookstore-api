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
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final MediaType PROBLEM_JSON = MediaType.parseMediaType("application/problem+json");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Resource not found: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Not Found")
                .status(404)
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex,
                                                                 HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Duplicate resource: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Conflict")
                .status(409)
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(StateConflictException.class)
    public ResponseEntity<ErrorResponse> handleStateConflict(StateConflictException ex,
                                                             HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] State conflict: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Conflict")
                .status(409)
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex,
                                                            HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Unauthorized: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Unauthorized")
                .status(401)
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(Exception ex,
                                                         HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Forbidden: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Forbidden")
                .status(403)
                .detail("Access denied")
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex,
                                                          HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Bad request: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Bad Request")
                .status(400)
                .detail(ex.getMessage())
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();

        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        logger.warn("[{}] Validation error: {}", requestId, fieldErrors);

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Validation Failed")
                .status(400)
                .detail("One or more fields are invalid")
                .instance(request.getRequestURI())
                .errors(fieldErrors)
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex,
                                                             HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Malformed JSON: {}", requestId, ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Bad Request")
                .status(400)
                .detail("Request body is missing or malformed")
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] Method not allowed: {}", requestId, ex.getMethod());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Method Not Allowed")
                .status(405)
                .detail("HTTP method " + ex.getMethod() + " is not supported for this endpoint")
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        String requestId = UUID.randomUUID().toString();
        logger.warn("[{}] No handler found: {} {}", requestId, ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Not Found")
                .status(404)
                .detail("Endpoint " + ex.getRequestURL() + " not found")
                .instance(ex.getRequestURL())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(PROBLEM_JSON)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();
        logger.error("[{}] Internal server error: {}", requestId, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .type("about:blank")
                .title("Internal Server Error")
                .status(500)
                .detail("An unexpected error occurred. Please try again later.")
                .instance(request.getRequestURI())
                .requestId(requestId)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(PROBLEM_JSON)
                .body(error);
    }
}