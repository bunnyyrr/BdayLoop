package ru.bdayloop.dao;

import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupDao {
    public Group create(Group group) throws SQLException{
        String sql = "INSERT INTO groups (name, created_by) VALUES(?, ?) RETURNING id";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, group.getName());
            ps.setInt(2, group.getCreatedBy());

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    int newId = rs.getInt("id");
                    return new Group(newId, group.getName(), group.getCreatedBy());
                }
                else throw new SQLException("INSERT не вернул сгенерированный id");
            }
        }
    }
    public Optional<Group> findById(int id) throws SQLException{
        String sql = "SELECT id, name, created_by FROM groups WHERE id =?";

        try(Connection conn=ConnectionManager.getConnection();
        PreparedStatement ps =conn.prepareStatement(sql)){
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Group> findAll() throws SQLException{
        String sql ="SELECT id, name, created_by FROM groups";

        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
            List<Group> result = new ArrayList<>();
            while(rs.next()){
                result.add(mapRow(rs));
            }
            return result;
        }
    }

    public void joinGroup(int userId, int groupId) throws SQLException{
        String sql ="INSERT INTO group_members(group_id, user_id) VALUES(?,?)";
        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps =conn.prepareStatement(sql)){
            ps.setInt(1, groupId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void subscribeToGroup(int subscriberId, int groupId) throws SQLException{
        String sql = "INSERT INTO group_subscriptions(subscriber_id, group_id) VALUES(?, ?)";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps =conn.prepareStatement(sql)) {
            ps.setInt(1, subscriberId);
            ps.setInt(2, groupId);
            ps.executeUpdate();
        }
    }

    public List<Integer> groupMembers(int groupId) throws SQLException{
        String sql = "SELECT user_id FROM group_members WHERE group_id =?";
        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, groupId);

            try(ResultSet rs = ps.executeQuery()) {
                List<Integer> result = new ArrayList<>();
                while (rs.next()){
                    result.add(rs.getInt("user_id"));
                }
                return result;
            }
        }
    }

    public void delete(int groupId, int requesterId) throws SQLException{
        Optional<Group> group = findById(groupId);

        if(group.isEmpty()){
            throw new SQLException(("Группа с id "+ groupId + " не найдена"));
        }
        Integer createdBy = group.get().getCreatedBy();
        if(createdBy == null || createdBy != requesterId){
            throw new SQLException(("Только создатель может удалить группу"));
        }

        String sql = " DELETE FROM groups WHERE id =?";
        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, groupId);
            ps.executeUpdate();
        }
    }

    private Group mapRow(ResultSet rs) throws SQLException{
        return new Group(rs.getInt("id"), rs.getString("name"), rs.getObject("created_by", Integer.class));
    }
}
