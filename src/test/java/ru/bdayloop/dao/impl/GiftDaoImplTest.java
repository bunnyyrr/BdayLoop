package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.dao.i.GiftDao;
import ru.bdayloop.model.Gift;
import ru.bdayloop.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GiftDaoImplTest extends DaoTestBase {

    @Autowired
    GiftDao giftDao;

    @Test
    void createAndFindByUserId_returnsSameGift() throws SQLException {
        User user = createUser("kate");

        giftDao.create(new Gift(0, user.getId(), "Книга"));

        List<Gift> gifts = giftDao.findByUserId(user.getId());
        assertEquals(1, gifts.size());
        assertEquals("Книга", gifts.get(0).getTitle());
    }

    @Test
    void delete_removesGift() throws SQLException {
        User user = createUser("kate");

        Gift created = giftDao.create(new Gift(0, user.getId(), "Книга"));

        giftDao.delete(created.getId());

        Optional<Gift> found = giftDao.findById(created.getId());
        assertTrue(found.isEmpty());
    }
}