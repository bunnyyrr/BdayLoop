package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.model.Message;
import ru.bdayloop.service.i.MessageService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.request.SendMessageRequest;
import ru.bdayloop.web.dto.response.MessageResponse;
import ru.bdayloop.web.exception.ExceptionHandler;

import java.io.IOException;
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
            JsonUtil.writeBody(resp, MessageResponse.from(created));
        } catch (Exception e) {
            ExceptionHandler.handle(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String subjectParam = req.getParameter("subjectId");
            if(subjectParam == null){
                throw new BadRequestException("Нужен параметр subjectId");
            }

            int subjectId =Integer.parseInt(subjectParam);
            int senderId = SessionUtil.requireUserId(req);

            List<Message> messages = messageService.getMessages(subjectId, senderId);
            JsonUtil.writeBody(resp, messages.stream().map(m -> MessageResponse.from(m)).toList());
        } catch (Exception e) {
            ExceptionHandler.handle(resp, e);
        }
    }
}