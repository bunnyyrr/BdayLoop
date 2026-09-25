package ru.bdayloop.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.User;

public class SessionUtil {
    private static final String USER_ID_KEY = "userId";
    private static final String ROLE_KEY = "role";

    public static void login(HttpServletRequest req, int userId, User.Role role){
        HttpSession session = req.getSession(true);
        session.setAttribute(USER_ID_KEY, userId);
        session.setAttribute(ROLE_KEY, role.name());
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

    public static void requireAdmin(HttpServletRequest req){
        requireUserId(req);
        HttpSession session = req.getSession(false);
        String role =(String) session.getAttribute(ROLE_KEY);
        if(!"ADMIN".equals(role)){
            throw new ForbiddenException("Требуются права администратора");
        }
    }

    public static User.Role currentRole(HttpServletRequest req){
        requireUserId(req);
        String role =(String) req.getSession(false).getAttribute(ROLE_KEY);
        return User.Role.valueOf(role);
    }
}
