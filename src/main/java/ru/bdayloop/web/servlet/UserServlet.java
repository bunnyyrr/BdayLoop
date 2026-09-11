package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.model.User;
import ru.bdayloop.service.UserService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.dto.LoginRequest;
import ru.bdayloop.web.dto.RegisterRequest;
import ru.bdayloop.web.dto.SubscribeRequest;
import ru.bdayloop.web.dto.UpdateUserRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
                JsonUtil.writeBody(resp, created);
            }
            else if (pathInfo.equals("/login")){
                LoginRequest body =JsonUtil.readBody(req, LoginRequest.class);
                User user =userService.login(body.username(), body.password());
                JsonUtil.writeBody(resp, user);
            }
            else{
                String[] parts =pathInfo.split("/");
                if(parts.length ==3 && parts[2].equals("subscribe")){
                    int targetId= Integer.parseInt(parts[1]);
                    SubscribeRequest body = JsonUtil.readBody(req, SubscribeRequest.class);
                    userService.subscribe(body.subscriberId(), targetId);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo =req.getPathInfo();

        try{
            if(pathInfo==null || pathInfo.equals("/")){
                String name = req.getParameter("name");
                List<User> users = userService.findByName(name);
                JsonUtil.writeBody(resp, users);
            }
            else {
                String[] parts = pathInfo.split("/");
                int id =Integer.parseInt(parts[1]);
                User user =userService.findById(id);
                JsonUtil.writeBody(resp, user);
            }
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo = req.getPathInfo();

        try{
            if(pathInfo==null || pathInfo.equals("/")){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int id = Integer.parseInt(pathInfo.split("/")[1]);

            User existing = userService.findById(id);
            UpdateUserRequest body = JsonUtil.readBody(req, UpdateUserRequest.class);

            User updated = new User(existing.getId(), body.name(), body.birthday(), body.username(), existing.getPasswordHash(), existing.getRole());

            User result =userService.update(updated);
            JsonUtil.writeBody(resp, result);
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

            if(parts.length == 3 && parts[2].equals("subscribe")){
                SubscribeRequest body = JsonUtil.readBody(req, SubscribeRequest.class);
                userService.unsubscribe(body.subscriberId(), id);
            }
            else {
                userService.delete(id);
            }

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }
}
