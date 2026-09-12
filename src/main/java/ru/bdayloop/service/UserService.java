package ru.bdayloop.service;

import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    User register(User user, String plainPassword) throws SQLException;
    User login(String username, String plainPassword) throws SQLException;
    User findById(int id) throws SQLException;
    List<User> findByName(String name) throws SQLException;
    User findByUsername(String username) throws SQLException;
    User update(User user) throws SQLException;
    void delete(int id) throws SQLException;
    void subscribe(int subscriberId, int targetId) throws SQLException;
    void unsubscribe(int subscriberId, int targetId) throws SQLException;
}
