package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundHandle(final NotFoundException e) {
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationHandle(final ValidationException e) {
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse nullHandler(final NullPointerException e) {
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse illegalHandler(final IllegalArgumentException e) {
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeHandler(final RuntimeException e) {
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }
}
