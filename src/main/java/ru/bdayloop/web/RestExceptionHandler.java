package ru.bdayloop.web;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.bdayloop.exception.*;

import java.nio.charset.StandardCharsets;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log= LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequest(BadRequestException e) {
        return text(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> unauthorized(UnauthorizedException e) {
        return text(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> forbidden(ForbiddenException e) {
        return text(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> notFound(NotFoundException e) {
        return text(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> conflict(ConflictException e) {
        return text(HttpStatus.CONFLICT, e.getMessage());
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> constraintViolation(ConstraintViolationException e) {
        return switch (String.valueOf(e.getSQLState())) {
            case "23505" -> text(HttpStatus.CONFLICT, "Такая запись уже существует");
            case "23503" -> text(HttpStatus.NOT_FOUND, "Связанная запись не найдена");
            case "23502", "23514" -> text(HttpStatus.BAD_REQUEST, "Некорректные данные");
            default -> {
                log.error("Ошибка БД, SQLState={}", e.getSQLState(), e);
                yield text(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
            }
        };
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> other(Exception e) {
        log.error("Необработанная ошибка", e);
        return text(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }


    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return textObject(HttpStatus.BAD_REQUEST, "Некорректный id");
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return textObject(HttpStatus.BAD_REQUEST, "Нужен параметр " + ex.getParameterName());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return textObject(HttpStatus.BAD_REQUEST, "Некорректное тело запроса");
    }


    private static ResponseEntity<String> text(HttpStatus status, String message){
        return ResponseEntity.status(status).contentType(new MediaType("text", "plain", StandardCharsets.UTF_8)).body(message);
    }

    private static ResponseEntity<Object> textObject(HttpStatus status, String message){
        return ResponseEntity.status(status).contentType(new MediaType("text", "plain", StandardCharsets.UTF_8)).body(message);
    }
}
