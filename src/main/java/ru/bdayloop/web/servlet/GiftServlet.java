package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.Gift;
import ru.bdayloop.service.GiftService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
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
            int userId = SessionUtil.requireUserId(req);

            Gift newGift = new Gift(0, userId, body.title());
            Gift created = giftService.create(newGift);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeBody(resp, created);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
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
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
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
            int requesterId = SessionUtil.requireUserId(req);

            giftService.delete(id, requesterId);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
