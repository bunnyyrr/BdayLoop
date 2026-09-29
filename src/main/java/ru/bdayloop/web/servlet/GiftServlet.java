package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.BadRequestException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Gift;
import ru.bdayloop.service.i.GiftService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.request.CreateGiftRequest;
import ru.bdayloop.web.dto.response.GiftResponse;
import ru.bdayloop.web.exception.ExceptionHandler;

import java.io.IOException;
import java.util.List;

public class GiftServlet extends HttpServlet {
    private final GiftService giftService;

    public GiftServlet(GiftService giftService) {
        this.giftService = giftService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            CreateGiftRequest body = JsonUtil.readBody(req, CreateGiftRequest.class);
            int userId = SessionUtil.requireUserId(req);

            Gift newGift = new Gift(0, userId, body.title());
            Gift created = giftService.create(newGift);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeBody(resp, GiftResponse.from(created));
        } catch (Exception e) {
            ExceptionHandler.handle(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String userIdParam= req.getParameter("userId");
            if(userIdParam == null){
                throw new BadRequestException("Нужен параметр userId");
            }
            int userId = Integer.parseInt(userIdParam);
            List<Gift> gifts = giftService.findByUserId(userId);
            JsonUtil.writeBody(resp, gifts.stream().map(g->GiftResponse.from(g)).toList());
        } catch (Exception e) {
            ExceptionHandler.handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new NotFoundException("Не найдено");
            }
            String[] parts = pathInfo.split("/");
            if (parts.length != 2) {
                throw new NotFoundException("Не найдено");
            }
            int id = Integer.parseInt(parts[1]);
            int requesterId = SessionUtil.requireUserId(req);

            giftService.delete(id, requesterId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            ExceptionHandler.handle(resp, e);
        }
    }
}