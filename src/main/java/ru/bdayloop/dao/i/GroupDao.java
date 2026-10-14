package ru.bdayloop.dao.i;

import java.sql.SQLException;
import java.util.List;

public interface GroupDao {
    void joinGroup(int userId, int groupId) throws SQLException;
    void subscribeToGroup(int subscriberId, int groupId) throws SQLException;
    List<Integer> groupMembers(int groupId) throws SQLException;
    void leaveGroup(int groupId, int userId) throws SQLException;
    void unsubscribeFromGroup(int subscriberId, int groupId) throws SQLException;
}