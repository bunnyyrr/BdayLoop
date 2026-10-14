package ru.bdayloop.service.i;

import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.util.List;

public interface GroupService {
    Group create(String name, int creatorId);
    Group findById(int id);
    List<Group> findByName(String name);
    List<Group> findAll();
    void joinGroup(int userId, int groupId) ;
    void subscribeToGroup(int subscriberId, int groupId) ;
    List<Integer> groupMembers(int groupId) ;
    void delete(int groupId, int requesterId, User.Role requesterRole);
    void leaveGroup(int groupId, int userId);
    void unsubscribeFromGroup(int subscriberId, int groupId);
    Group update(int groupId, String newName, int requesterId, User.Role requesterRole);
    List<Group> findByMember(int userId);
    List<Group> findBySubscriber(int subscriberId);
}
