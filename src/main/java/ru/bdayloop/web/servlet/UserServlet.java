package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.User;
import ru.bdayloop.service.UserService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    private final UserService userService;

    public UserServlet(UserService userService){
        this.userService=userService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo =req.getPathInfo();

        try{
            if(pathInfo ==null || pathInfo.equals("/")){
                RegisterRequest body = JsonUtil.readBody(req, RegisterRequest.class);
                User newUser = new User(0, body.name(), body.birthday(), body.username(), null, User.Role.USER);
                User created = userService.register(newUser, body.password());
                resp.setStatus(HttpServletResponse.SC_CREATED);
                JsonUtil.writeBody(resp, UserResponse.from(created));
            }
            else if (pathInfo.equals("/login")){
                LoginRequest body =JsonUtil.readBody(req, LoginRequest.class);
                User user =userService.login(body.username(), body.password());
                SessionUtil.login(req, user.getId(), user.getRole());
                JsonUtil.writeBody(resp, UserResponse.from(user));
            }
            else if(pathInfo.equals("/logout")){
                SessionUtil.logout(req);
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
            else if (pathInfo.equals("/import")) {
            SessionUtil.requireAdmin(req);
            ImportUserRequest[] body = JsonUtil.readBody(req, ImportUserRequest[].class);
            List<User> created = userService.importUsers(Arrays.asList(body));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.writeBody(resp, created.stream().map(u -> UserResponse.from(u)).toList());
            }
            else{
                String[] parts =pathInfo.split("/");
                if(parts.length ==3 && parts[2].equals("subscribe")){
                    int targetId= Integer.parseInt(parts[1]);
                    int subscriberId = SessionUtil.requireUserId(req);
                    userService.subscribe(subscriberId, targetId);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo =req.getPathInfo();

        try{
            int requesterId = SessionUtil.requireUserId(req);
            if(pathInfo==null || pathInfo.equals("/")){
                String name = req.getParameter("name");
                if(name==null) name="";
                List<User> users = userService.findByName(name);
                JsonUtil.writeBody(resp, users.stream().map(u -> UserResponse.from(u)).toList());
            }
            else {
                String[] parts = pathInfo.split("/");
                int id =Integer.parseInt(parts[1]);

                if(parts.length == 3 && parts[2].equals("subscribe")){
                    boolean subscribed = userService.isSubscribedDirectly(requesterId, id);
                    JsonUtil.writeBody(resp, new SubscriptionStatusResponse(subscribed));
                }
                else {
                    User user = userService.findById(id);
                    JsonUtil.writeBody(resp, UserResponse.from(user));
                }
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int id = Integer.parseInt(pathInfo.split("/")[1]);
            int requesterId = SessionUtil.requireUserId(req);

            User existing = userService.findById(id);
            UpdateUserRequest body = JsonUtil.readBody(req, UpdateUserRequest.class);

            User updated = new User(existing.getId(), body.name(), body.birthday(), body.username(), existing.getPasswordHash(), existing.getRole());

            User result = userService.update(updated, requesterId);
            JsonUtil.writeBody(resp, UserResponse.from(result));
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch(SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo =req.getPathInfo();

        try{
            if(pathInfo==null || pathInfo.equals("/")){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String[] parts =pathInfo.split("/");
            int id = Integer.parseInt((parts[1]));
            int requesterId = SessionUtil.requireUserId(req);

            if(parts.length == 3 && parts[2].equals("subscribe")){
                userService.unsubscribe(requesterId, id);
            }
            else {
                userService.delete(id, requesterId);
            }

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }
}
