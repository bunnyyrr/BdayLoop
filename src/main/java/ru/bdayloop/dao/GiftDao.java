package ru.bdayloop.dao;

import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Gift;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GiftDao {
    public Gift create(Gift gift) throws SQLException{
        String sql = "INSERT INTO gifts(user_id, title) VALUES (?, ?) RETURNING id";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, gift.getUserId());
            ps.setString(2, gift.getTitle());

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    int newId = rs.getInt("id");
                    Gift newGift = new Gift (newId, gift.getUserId(), gift.getTitle());
                    return newGift;
                }
                else throw new SQLException("INSERT не вернул сгенерированный id");
            }
        }
    }

    public void delete(int id) throws SQLException{
        String sql = "DELETE FROM gifts WHERE id =?";

        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps =conn.prepareStatement(sql)){
            ps.setInt(1, id);

            int rowsAffected= ps.executeUpdate();
            if(rowsAffected ==0){
                throw new SQLException("Подарок с id "+ id+ " не найден");
            }
        }
    }

    public List<Gift> findByUserId(int userId) throws SQLException{
        String sql ="SELECT id, user_id, title FROM gifts WHERE user_id =?";

        try(Connection conn = ConnectionManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, userId);

            try(ResultSet rs = ps.executeQuery()){
                List<Gift> result = new ArrayList<>();
                while(rs.next()){
                    result.add(new Gift(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title")));
                }
                return result;
            }
        }
    }
}
