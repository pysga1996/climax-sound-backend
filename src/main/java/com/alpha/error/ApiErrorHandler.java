package com.alpha.error;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestControllerAdvice(annotations = {RestController.class})
public class ApiErrorHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAllException(Exception ex, WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(9999, ex.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ApiError handleAllException(AccessDeniedException ex, WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(3500, ex.getLocalizedMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleBusinessException(BusinessException ex, WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(ex.getCode(), ex.getLocalizedMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Entity not found: ", ex);
        return new ApiError(3000, ex.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleEntityExistedException(EntityExistsException ex, WebRequest request) {
        log.error("Entity existed: ", ex);
        return new ApiError(2500, ex.getMessage());
    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidTokenException(InvalidBearerTokenException ex,
        WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(2000, ex.getLocalizedMessage());
    }

    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        HttpStatus status,
        @NonNull WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        //Get all errors
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList());

        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> constraintViolationException(ConstraintViolationException ex) {
        log.error(ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        return body;
    }

}
