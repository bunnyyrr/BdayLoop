package ru.bdayloop.service.impl;

import ru.bdayloop.dao.GiftDao;
import ru.bdayloop.exception.ForbiddenException;
import ru.bdayloop.exception.NotFoundException;
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
    public void delete(int id, int requesterId) throws SQLException {
        Gift gift = giftDao.findById(id)
                .orElseThrow(() -> new NotFoundException("Подарок с id " + id + " не найден"));
        if (gift.getUserId() != requesterId) {
            throw new ForbiddenException("Можно удалить только свой подарок");
        }
        giftDao.delete(id);
    }

    @Override
    public List<Gift> findByUserId(int userId) throws SQLException{
        return giftDao.findByUserId(userId);
    }
}
