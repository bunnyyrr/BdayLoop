package ru.bdayloop.service.i;

import ru.bdayloop.model.User;
import ru.bdayloop.service.command.UserRegistration;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    User register(User user, String plainPassword) throws SQLException;
    User login(String username, String plainPassword) throws SQLException;
    User findById(int id) throws SQLException;
    List<User> findByName(String name) throws SQLException;
    User findByUsername(String username) throws SQLException;
    User update(User user, int requesterId) throws SQLException;
    void delete(int id, int requesterId) throws SQLException;
    void subscribe(int subscriberId, int targetId) throws SQLException;
    void unsubscribe(int subscriberId, int targetId) throws SQLException;
    List<User> importUsers(List<UserRegistration> registrations) throws SQLException;
    boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException;
}
