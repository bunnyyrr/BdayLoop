package ru.bdayloop.dao;

import ru.bdayloop.model.User;
import ru.bdayloop.db.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT id, name, birthday, username, password_hash, role FROM users WHERE id = ?";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next()? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, name, birthday, username, password_hash, role FROM users WHERE username = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public void update(User user) throws SQLException{
        String sql = "UPDATE users SET name =?, birthday = ?, username = ?, password_hash = ?, role = ? WHERE id =?";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, user.getName());
            ps.setObject(2, user.getBirthday());
            ps.setString(3, user.getUsername());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole().name());
            ps.setInt(6, user.getId());

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 0){
                throw new SQLException("Пользователь с id " + user.getId()+ " не найден для обновления");
            }
        }
    }

    public void delete(int id) throws SQLException{
        String sql ="DELETE FROM users WHERE id = ?";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 0){
                throw new SQLException("Пользователь с id " + id +" не найден для удаления");
            }
        }
    }
    public void subscribe(int subscriberId, int targetId) throws SQLException{
        String sql = "INSERT INTO subscriptions(subscriber_id, target_id) VALUES(?,?)";
        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);
            ps.executeUpdate();
        }
    }

    public void unsubscribe(int subscriberId, int targetId) throws SQLException{
        String sql = "DELETE FROM subscriptions WHERE subscriber_id=? AND target_id=?";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);
            ps.executeUpdate();
        }
    }

    public List<User> findByName(String name) throws SQLException{
        String sql = "SELECT id, name, birthday, username, password_hash, role FROM users WHERE name ILIKE ?";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%"+ name +"%");

            try(ResultSet rs = ps.executeQuery()){
                List<User> result = new ArrayList<>();
                while(rs.next()){
                    result.add(mapRow(rs));
                }
                return result;
            }
        }
    }

    private User mapRow(ResultSet rs) throws SQLException{
        return new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getObject("birthday", LocalDate.class),
                rs.getString("username"),
                rs.getString("password_hash"),
                User.Role.valueOf(rs.getString("role"))
        );
    }
}
