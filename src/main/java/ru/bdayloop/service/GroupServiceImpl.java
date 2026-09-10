package ru.bdayloop.service;

import ru.bdayloop.dao.GroupDao;
import ru.bdayloop.model.Group;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GroupServiceImpl implements GroupService{
    private final GroupDao groupDao;
    public GroupServiceImpl (GroupDao groupDao){
        this.groupDao = groupDao;
    }

    @Override
    public void delete(int groupId, int requesterId) throws SQLException{
        Optional<Group> group = groupDao.findById(groupId);

        if(group.isEmpty()){
            throw new SQLException(("Группа с id "+ groupId + " не найдена"));
        }
        Integer createdBy = group.get().getCreatedBy();
        if(createdBy == null || createdBy != requesterId){
            throw new SQLException(("Только создатель может удалить группу"));
        }
        groupDao.delete(groupId);
    }

    @Override
    public Group create(Group group) throws SQLException{
        return groupDao.create(group);
    }

    @Override
    public Group findById(int id) throws SQLException{
        return groupDao.findById(id).orElseThrow(()-> new SQLException("Группа с id "+ id + " не найдена"));
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
    public void update(Group group) throws SQLException{
        groupDao.update(group);
    }
}
