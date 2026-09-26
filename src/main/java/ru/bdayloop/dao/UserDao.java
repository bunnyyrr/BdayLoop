package ru.bdayloop.dao;

import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user) throws SQLException;
    Optional<User> findById(int id) throws SQLException;
    Optional<User> findByUsername(String username) throws SQLException;
    void update(User user) throws SQLException;
    void delete(int id) throws SQLException;
    void subscribe(int subscriberId, int targetId) throws SQLException;
    void unsubscribe(int subscriberId, int targetId) throws SQLException;
    List<User> findByName(String name) throws SQLException;
    boolean isSubscribedDirectlyOrViaGroup(int subscriberId, int targetId) throws SQLException;
    boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException;
}
