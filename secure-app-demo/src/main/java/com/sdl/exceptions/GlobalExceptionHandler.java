package com.sdl.exceptions;

import com.sdl.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@RestControllerAdvice(basePackages = "com.sdl.web")
public class GlobalExceptionHandler {

    private ResponseEntity<ApiResponse> buildResponse(
            HttpStatus status,
            String message,
            Map<String, ?> errors,
            HttpServletRequest request
    ) {
        ApiResponse response = ApiResponse.builder()
                .statusCode(status.value())
                .message(message)
                .errors(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    private String extractDuplicateEntryMessage(String rootMessage) {
        if (rootMessage.contains("Duplicate entry")) {
            Pattern pattern = Pattern.compile("Duplicate entry '(.*?)' for key '(.*?)'");
            Matcher matcher = pattern.matcher(rootMessage);
            if (matcher.find()) {
                String value = matcher.group(1);
                String constraint = matcher.group(2);
                String field = constraint.contains(".") ?
                        constraint.substring(constraint.lastIndexOf('.') + 1) : constraint;
                return String.format("A record with this %s '%s' already exists", field, value);
            }
        } else if (rootMessage.contains("unique constraint")) {
            Pattern pattern = Pattern.compile("unique constraint \"(.*?)\"");
            Matcher matcher = pattern.matcher(rootMessage);
            if (matcher.find()) {
                String constraint = matcher.group(1);
                return String.format("Duplicate value violates unique constraint: %s", constraint);
            }
        }

        return "A record with the same unique identifier already exists";
    }

    private HttpStatus determineHttpStatus(IllegalStateException ex) {
        String message = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (message.contains("already initialized") ||
                message.contains("already started") ||
                message.contains("already exists")) {
            return HttpStatus.CONFLICT;
        }

        if (message.contains("not initialized") ||
                message.contains("not started") ||
                message.contains("not available")) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

        if (message.contains("invalid state") ||
                message.contains("incorrect state")) {
            return HttpStatus.PRECONDITION_FAILED;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getUserFriendlyMessage(IllegalStateException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "";

        if (message.contains("already initialized")) {
            return "Operation cannot be completed because the resource is already initialized";
        }

        if (message.contains("not initialized")) {
            return "Operation cannot be completed because the required resource is not ready";
        }

        if (message.contains("already exists")) {
            return "The resource you're trying to create already exists";
        }

        if (message.contains("not found") || message.contains("does not exist")) {
            return "The requested resource could not be found";
        }

        return "The operation cannot be completed due to an invalid system state";
    }








    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> String.valueOf(v.getPropertyPath()),
                        ConstraintViolation::getMessage,
                        (a, b) -> a
                ));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("parameter", ex.getName());
        errors.put("rejectedValue", ex.getValue());
        errors.put("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter type", errors, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("parameter", ex.getParameterName());
        errors.put("expectedType", ex.getParameterType());

        return buildResponse(HttpStatus.BAD_REQUEST, "Missing required request parameter", errors, request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse> handleMissingPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("part", ex.getRequestPartName());

        return buildResponse(HttpStatus.BAD_REQUEST, "Missing required request part", errors, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request

    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON or request body is not readable",null, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("contentType", ex.getContentType());
        errors.put("supported", ex.getSupportedMediaTypes().stream().map(MediaType::toString).collect(Collectors.toList()));

        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Content-Type", errors, request);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResponse> handleNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.NOT_ACCEPTABLE, "Not acceptable 'Accept' header",null, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("method", ex.getMethod());
        errors.put("supported", ex.getSupportedHttpMethods());

        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "HTTP method not allowed", errors, request);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiResponse> handleNotWritable(
            HttpMessageNotWritableException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to write response body",null, request);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse> handleConversion(
            HttpMessageConversionException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Request/response conversion error",null, request);
    }



    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = extractDuplicateEntryMessage(ex.getRootCause().getMessage());
        return buildResponse(HttpStatus.CONFLICT, message,null, request);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String message = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return buildResponse(status, message,null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleAnyUnhandled(
            Exception ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error",null, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", null, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request
    ) {

        HttpStatus status = determineHttpStatus(ex);

        String message = getUserFriendlyMessage(ex);

        return buildResponse(status, message, null, request);
    }


}
