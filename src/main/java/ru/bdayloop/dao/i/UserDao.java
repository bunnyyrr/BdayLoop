package ru.bdayloop.dao.i;

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
    List<User> findByName(String name) throws SQLException;
}
