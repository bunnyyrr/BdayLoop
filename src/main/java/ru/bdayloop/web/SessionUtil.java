package ru.bdayloop.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.bdayloop.exception.UnauthorizedException;

public class SessionUtil {
    private static final String USER_ID_KEY = "userId";

    public static void login(HttpServletRequest req, int userId){
        HttpSession session = req.getSession(true);
        session.setAttribute(USER_ID_KEY, userId);
    }

    public static void logout(HttpServletRequest req){
        HttpSession session = req.getSession(false);
        if(session != null){
            session.invalidate();
        }
    }

    public static int requireUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if(session == null || session.getAttribute(USER_ID_KEY) == null){
            throw new UnauthorizedException("Требуется авторизация");
        }
        return (int) session.getAttribute(USER_ID_KEY);
    }
}
