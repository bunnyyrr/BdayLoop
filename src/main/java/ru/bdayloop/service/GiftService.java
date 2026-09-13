package ru.bdayloop.service;

import ru.bdayloop.model.Gift;

import java.sql.SQLException;
import java.util.List;

public interface GiftService {
    Gift create(Gift gift) throws SQLException;
    void delete(int id) throws SQLException;
    List<Gift> findByUserId(int userId) throws SQLException;
}
