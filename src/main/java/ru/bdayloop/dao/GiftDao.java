package ru.bdayloop.dao;

import ru.bdayloop.model.Gift;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface GiftDao {
    Gift create(Gift gift) throws SQLException;
    void delete(int id) throws SQLException;
    List<Gift> findByUserId(int userId) throws SQLException;
    Optional<Gift> findById(int id) throws SQLException;
}
