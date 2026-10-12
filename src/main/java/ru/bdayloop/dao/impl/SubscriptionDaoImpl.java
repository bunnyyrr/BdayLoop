package ru.bdayloop.dao.impl;

import org.springframework.stereotype.Repository;
import ru.bdayloop.dao.i.SubscriptionDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SubscriptionDaoImpl implements SubscriptionDao {
    private final DataSource dataSource;

    public SubscriptionDaoImpl(DataSource dataSource){
        this.dataSource= dataSource;
    }

    @Override
    public void subscribe(int subscriberId, int targetId) throws SQLException{
        String sql = "INSERT INTO subscriptions(subscriber_id, target_id) VALUES(?,?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);
            ps.executeUpdate();
        }
    }

    @Override
    public void unsubscribe(int subscriberId, int targetId) throws SQLException{
        String sql = "DELETE FROM subscriptions WHERE subscriber_id=? AND target_id=?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean isSubscribedDirectlyOrViaGroup(int subscriberId, int targetId) throws SQLException {
        String sql = """
            SELECT EXISTS (
                SELECT 1 FROM subscriptions WHERE subscriber_id = ? AND target_id = ?
            ) OR EXISTS (
                SELECT 1 FROM group_subscriptions gs
                JOIN group_members gm ON gm.group_id = gs.group_id
                WHERE gs.subscriber_id = ? AND gm.user_id = ?
            )
            """;

        try(Connection conn= dataSource.getConnection();
            PreparedStatement ps =conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);
            ps.setInt(3, subscriberId);
            ps.setInt(4, targetId);

            try(ResultSet rs = ps.executeQuery()){
                rs.next();
                return rs.getBoolean(1);
            }
        }
    }

    @Override
    public boolean isSubscribedDirectly(int subscriberId, int targetId) throws SQLException{
        String sql = "SELECT 1 FROM subscriptions WHERE subscriber_id = ? AND target_id = ?";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subscriberId);
            ps.setInt(2, targetId);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }

}
