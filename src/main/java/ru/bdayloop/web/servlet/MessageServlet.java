package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.Message;
import ru.bdayloop.service.MessageService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.SendMessageRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MessageServlet extends HttpServlet {
    private final MessageService messageService;

    public MessageServlet(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            SendMessageRequest body = JsonUtil.readBody(req, SendMessageRequest.class);
            int senderId = SessionUtil.requireUserId(req);
            Message newMessage = new Message(0, body.subjectId(), senderId, body.text(), null);
            Message created = messageService.sendMessage(newMessage);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeBody(resp, created);
        }catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String subjectParam = req.getParameter("subjectId");
            if(subjectParam == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Нужны параметры subjectId");
                return;
            }

            int subjectId =Integer.parseInt(subjectParam);
            int senderId = SessionUtil.requireUserId(req);

            List<Message> messages = messageService.getMessages(subjectId, senderId);
            JsonUtil.writeBody(resp, messages);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }
}
