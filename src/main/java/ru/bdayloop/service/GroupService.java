package ru.bdayloop.service;

import ru.bdayloop.model.Group;

import java.sql.SQLException;
import java.util.List;

public interface GroupService {
    Group create(Group group) throws SQLException;
    Group findById(int id) throws SQLException;
    List<Group> findByName(String name) throws SQLException;
    List<Group> findAll() throws SQLException;
    void joinGroup(int userId, int groupId) throws SQLException;
    void subscribeToGroup(int subscriberId, int groupId) throws SQLException;
    List<Integer> groupMembers(int groupId) throws SQLException;
    void delete(int groupId, int requesterId) throws SQLException;
    void leaveGroup(int groupId, int userId) throws SQLException;
    void unsubscribeFromGroup(int subscriberId, int groupId) throws SQLException;
    void update(Group group) throws SQLException;
}
