package ru.bdayloop.dao;

import ru.bdayloop.model.User;
import ru.bdayloop.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    public User create (User user) throws SQLException {
        String sql = "INSERT INTO Users(name, birthday, username, password_hash, role) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setObject(2, user.getBirthday());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int newId = rs.getInt("id");
                    User newUser = new User(newId, user.getName(), user.getBirthday(), user.getUsername(), user.getPasswordHash(), user.getRole());
                    return newUser;
                } else {
                    throw new SQLException("INSERT не вернул сгенерированный id");
                }
            }
        }
    }
}
