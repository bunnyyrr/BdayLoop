package ru.bdayloop.dao;

import ru.bdayloop.db.ConnectionManager;
import ru.bdayloop.model.Message;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    public Message create(Message message) throws SQLException{
        String sql ="INSERT INTO messages(subject_user_id, sender_id, text) VALUES(?,?,?) RETURNING id, created_at";

        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, message.getSubjectUserId());
            ps.setInt(2, message.getSenderId());
            ps.setString(3, message.getText());

            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    int newId = rs.getInt("id");
                    LocalDateTime time = rs.getTimestamp("created_at").toLocalDateTime();
                    return new Message(newId, message.getSubjectUserId(), message.getSenderId(), message.getText(), time);
                }
                else throw new SQLException("INSERT не вернул сгенерированный id");
            }
        }
    }

    public List<Message> findBySubjectUserId(int subjectUserId) throws SQLException{
        String sql = "SELECT id, sender_id, text, created_at FROM messages WHERE subject_user_id=? ORDER BY created_at";
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, subjectUserId);

            try(ResultSet rs = ps.executeQuery()){
                List<Message> messages = new ArrayList<>();
                while(rs.next()){
                    messages.add(new Message(rs.getInt("id"),  subjectUserId, rs.getInt("sender_id"), rs.getString("text"), rs.getTimestamp("created_at").toLocalDateTime()));
                }
                return messages;
            }
        }
    }
}
