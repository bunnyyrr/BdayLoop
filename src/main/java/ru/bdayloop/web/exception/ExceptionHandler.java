package ru.bdayloop.web.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.ConflictException;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;

import java.io.IOException;
import java.sql.SQLException;

public class ExceptionHandler {
    public static void handle(HttpServletResponse resp, Exception e) throws IOException{
        switch (e) {
            case BadRequestException ex -> write(resp, 400, ex.getMessage());
            case UnauthorizedException ex -> write(resp, 401, ex.getMessage());
            case ForbiddenException ex -> write(resp, 403, ex.getMessage());
            case NotFoundException ex -> write(resp, 404, ex.getMessage());
            case ConflictException ex -> write(resp, 409, ex.getMessage());
            case NumberFormatException ex -> write(resp, 400, "Некорректный id");
            case JsonProcessingException ex -> write(resp, 400, "Некорректное тело запроса");
            case SQLException ex -> handleSql(resp, ex);
            default -> {
                e.printStackTrace();
                write(resp, 500, "Внутренняя ошибка сервера");
            }
        }
    }

    private static void handleSql(HttpServletResponse resp, SQLException e) throws IOException {
        switch (String.valueOf(e.getSQLState())) {
            case "23505" -> write(resp, 409, "Такая запись уже существует");
            case "23503" -> write(resp, 404, "Связанная запись не найдена");
            case "23502", "23514" -> write(resp, 400, "Некорректные данные");
            default -> {
                e.printStackTrace();
                write(resp, 500, "Внутренняя ошибка сервера");
            }
        }
    }

    private static void write(HttpServletResponse resp, int status, String message) throws IOException{
        resp.setStatus(status);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().write(message);
    }
}
