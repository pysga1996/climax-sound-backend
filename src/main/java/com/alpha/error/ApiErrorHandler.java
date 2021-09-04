package com.alpha.error;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Log4j2
@RestControllerAdvice(annotations = {RestController.class})
@SuppressWarnings("deprecation")
public class ApiErrorHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleAllException(Exception ex, WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(9999, ex.getLocalizedMessage());
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
        return new ApiError(3000, ex.getLocalizedMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiError handleEntityExistedException(EntityExistsException ex, WebRequest request) {
        log.error("Entity existed: ", ex);
        return new ApiError(2500, ex.getLocalizedMessage());
    }

//    @ExceptionHandler(InvalidTokenException.class)
//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    public ApiError handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
//        log.error("Exception: {}, user: {}", ex, request.getRemoteUser());
//        return new ApiError(2000, ex.getLocalizedMessage());
//    }

    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidTokenException(InvalidBearerTokenException ex,
        WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(2000, ex.getLocalizedMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ApiError handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.error("Exception: ", ex);
        return new ApiError(1500, ex.getLocalizedMessage());
    }

}
