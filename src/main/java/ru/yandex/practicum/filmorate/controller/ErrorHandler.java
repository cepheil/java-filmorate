package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)   //409
    public ErrorResponse handlerDuplicatedDataException(DuplicatedDataException e) {
        log.warn("DuplicatedDataException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    public ErrorResponse handlerNotFoundException(NotFoundException e) {
        log.warn("NotFoundException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public ErrorResponse handlerValidationException(ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        return buildErrorResponse(e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  //422
    public ErrorResponse handlerConditionsNotMetException(ConditionsNotMetException e) {
        log.warn("ConditionsNotMetException: {}", e.getMessage());
        return buildErrorResponse(e);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        return buildErrorResponse(e);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)   //400
    public ErrorResponse handlerConstraintViolationException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        return buildErrorResponse(e);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)   //500
    public ErrorResponse handlerThrowable(Throwable e) {
        log.error("Unexpected error: ", e);
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage());
    }


    private ErrorResponse buildErrorResponse(Exception e) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage());
    }

}
