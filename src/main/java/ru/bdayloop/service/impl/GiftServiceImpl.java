package ru.bdayloop.service.impl;

import ru.bdayloop.dao.GiftDao;
import ru.bdayloop.model.Gift;
import ru.bdayloop.service.GiftService;

import java.sql.SQLException;
import java.util.List;

public class GiftServiceImpl implements GiftService {
    private final GiftDao giftDao;
    public GiftServiceImpl(GiftDao giftDao){
        this.giftDao= giftDao;
    }

    @Override
    public Gift create(Gift gift) throws SQLException{
        return giftDao.create(gift);
    }

    @Override
    public void delete(int id) throws SQLException{
        giftDao.delete(id);
    }

    @Override
    public List<Gift> findByUserId(int userId) throws SQLException{
        return giftDao.findByUserId(userId);
    }
}
