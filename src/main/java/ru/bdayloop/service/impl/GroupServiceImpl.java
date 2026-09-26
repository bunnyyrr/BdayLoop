package ru.bdayloop.service.impl;

import ru.bdayloop.dao.GroupDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;
import ru.bdayloop.service.GroupService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GroupServiceImpl implements GroupService {
    private final GroupDao groupDao;
    public GroupServiceImpl (GroupDao groupDao){
        this.groupDao = groupDao;
    }

    @Override
    public void delete(int groupId, int requesterId, User.Role requesterRole) throws SQLException{
        permissionCheck(groupId, requesterId, requesterRole);
        groupDao.delete(groupId);
    }

    @Override
    public Group create(Group group) throws SQLException{
        return groupDao.create(group);
    }

    @Override
    public Group findById(int id) throws SQLException{
        return groupDao.findById(id).orElseThrow(()-> new NotFoundException("Группа с id "+ id + " не найдена"));
    }

    @Override
    public List<Group> findByName(String name) throws SQLException{
        return groupDao.findByName(name);
    }

    @Override
    public List<Group> findAll() throws SQLException{
        return groupDao.findAll();
    }

    @Override
    public void joinGroup(int userId, int groupId) throws SQLException{
        groupDao.joinGroup(userId, groupId);
    }

    @Override
    public void subscribeToGroup(int subscriberId, int groupId) throws SQLException{
        groupDao.subscribeToGroup(subscriberId, groupId);
    }

    @Override
    public List<Integer> groupMembers(int groupId) throws SQLException {
        return groupDao.groupMembers(groupId);
    }

    @Override
    public void leaveGroup(int groupId, int userId) throws SQLException{
        groupDao.leaveGroup(groupId, userId);
    }

    @Override
    public void unsubscribeFromGroup(int subscriberId, int groupId) throws SQLException{
        groupDao.unsubscribeFromGroup(subscriberId, groupId);
    }

    @Override
    public Group update(int groupId, String newName, int requesterId, User.Role requesterRole) throws SQLException{
        Group group = permissionCheck(groupId, requesterId, requesterRole);
        Group updated = new Group(group.getId(), newName, group.getCreatedBy());
        groupDao.update(updated);
        return updated;
    }

    @Override
    public List<Group> findByMember(int userId) throws SQLException{
        return groupDao.findByMember(userId);
    }

    @Override
    public List<Group> findBySubscriber(int subscriberId) throws SQLException{
        return groupDao.findBySubscriber(subscriberId);
    }

    private Group permissionCheck(int groupId, int requesterId, User.Role requesterRole) throws SQLException{
        Group group = findById(groupId);
        boolean isCreator = group.getCreatedBy() != null && group.getCreatedBy() == requesterId;
        if(!isCreator && requesterRole!=User.Role.ADMIN){
            throw new ForbiddenException("Управлять группой может только её создатель или администратор");
        }
        return group;
    }
}
