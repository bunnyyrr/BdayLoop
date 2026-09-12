package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.model.Gift;
import ru.bdayloop.service.GiftService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.dto.CreateGiftRequest;

import java.io.IOException;
import java.sql.SQLException;
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
            Gift newGift = new Gift(0, body.userId(), body.title());
            Gift created = giftService.create(newGift);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeBody(resp, created);
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String userIdParam= req.getParameter("userId");
            if(userIdParam == null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Нужен параметр userId");
                return;
            }
            int userId = Integer.parseInt(userIdParam);
            List<Gift> gifts = giftService.findByUserId(userId);
            JsonUtil.writeBody(resp, gifts);
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный userId");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            int id =Integer.parseInt(pathInfo.split("/")[1]);
            giftService.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }
}
