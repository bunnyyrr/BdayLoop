package ru.bdayloop.dao.impl;

import org.springframework.stereotype.Repository;
import ru.bdayloop.dao.i.GroupDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GroupDaoImpl implements GroupDao {
    private final DataSource dataSource;

    public GroupDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void joinGroup(int userId, int groupId) throws SQLException {
        String sql = "INSERT INTO group_members(group_id, user_id) VALUES(?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public void subscribeToGroup(int subscriberId, int groupId) throws SQLException {
        String sql = "INSERT INTO group_subscriptions(subscriber_id, group_id) VALUES(?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subscriberId);
            ps.setInt(2, groupId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Integer> groupMembers(int groupId) throws SQLException {
        String sql = "SELECT user_id FROM group_members WHERE group_id =?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rs.getInt("user_id"));
                }
                return result;
            }
        }
    }

    @Override
    public void leaveGroup(int groupId, int userId) throws SQLException {
        String sql = "DELETE FROM group_members WHERE group_id=? AND user_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public void unsubscribeFromGroup(int subscriberId, int groupId) throws SQLException {
        String sql = "DELETE FROM group_subscriptions WHERE subscriber_id=? AND group_id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subscriberId);
            ps.setInt(2, groupId);
            ps.executeUpdate();
        }
    }
}