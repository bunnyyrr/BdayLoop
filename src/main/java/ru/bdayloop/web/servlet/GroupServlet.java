package ru.bdayloop.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.exception.UnauthorizedException;
import ru.bdayloop.model.Group;
import ru.bdayloop.service.GroupService;
import ru.bdayloop.web.JsonUtil;
import ru.bdayloop.web.SessionUtil;
import ru.bdayloop.web.dto.CreateGroupRequest;
import ru.bdayloop.web.dto.UpdateGroupRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GroupServlet extends HttpServlet {
    private final GroupService groupService;
    public GroupServlet(GroupService groupService){
        this.groupService= groupService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo= req.getPathInfo();

        try{
            if(pathInfo ==null || pathInfo.equals("/")){
                CreateGroupRequest body = JsonUtil.readBody(req, CreateGroupRequest.class);
                int creatorId = SessionUtil.requireUserId(req);
                Group newGroup = new Group(0, body.name(), creatorId);
                Group created = groupService.create(newGroup);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                JsonUtil.writeBody(resp, created);
            }
            else {
                String[] parts = pathInfo.split("/");
                int groupId = Integer.parseInt(parts[1]);
                int userId = SessionUtil.requireUserId(req);

                if (parts.length == 3 && parts[2].equals("join")){
                    groupService.joinGroup(userId, groupId);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
                else if (parts.length ==3 && parts[2].equals("subscribe")){
                    groupService.subscribeToGroup(userId, groupId);
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo= req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                String name= req.getParameter("name");
                List<Group> groups = (name == null) ? groupService.findAll() : groupService.findByName(name);
                JsonUtil.writeBody(resp, groups);
            }
            else {
                String[] parts = pathInfo.split("/");
                int id =Integer.parseInt(parts[1]);

                if(parts.length == 3 && parts[2].equals("members")){
                    List<Integer> members = groupService.groupMembers(id);
                    JsonUtil.writeBody(resp, members);
                }
                else {
                    Group group = groupService.findById(id);
                    JsonUtil.writeBody(resp, group);
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
        } catch (NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int id =Integer.parseInt(pathInfo.split("/")[1]);
            SessionUtil.requireUserId(req);

            Group existing = groupService.findById(id);
            UpdateGroupRequest body = JsonUtil.readBody(req, UpdateGroupRequest.class);

            Group updated = new Group(existing.getId(), body.name(), existing.getCreatedBy());
            groupService.update(updated);
            JsonUtil.writeBody(resp, updated);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (ForbiddenException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (UnauthorizedException e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
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

            String[] parts = pathInfo.split("/");
            int id = Integer.parseInt(parts[1]);
            int userId = SessionUtil.requireUserId(req);

            if (parts.length == 3 && parts[2].equals("join")){
                groupService.leaveGroup(id, userId);
            }
            else if (parts.length == 3 && parts[2].equals("subscribe")){
                groupService.unsubscribeFromGroup(userId, id);
            }
            else{
                groupService.delete(id, userId);
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
        } catch (NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный id");
        }
    }
}
