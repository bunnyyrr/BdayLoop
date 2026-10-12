package ru.bdayloop.dao.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.bdayloop.dao.i.GroupDao;
import ru.bdayloop.dao.i.SubscriptionDao;
import ru.bdayloop.model.Group;
import ru.bdayloop.model.User;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionDaoImplTest extends DaoTestBase{
    @Autowired
    SubscriptionDao subscriptionDao;
    @Autowired
    GroupDao groupDao;

    @Test
    void isSubscribedDirectlyOrViaGroup_directSubscription_returnsTrue() throws SQLException {
        User anya = createUser("anya");
        User kate = createUser("kate");
        subscriptionDao.subscribe(anya.getId(), kate.getId());

        assertTrue(subscriptionDao.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_viaGroupSubscription_returnsTrue() throws SQLException {
        User anya = createUser("anya");
        User kate = createUser("kate");
        Group group = groupDao.create(new Group(0, "972501 ТГУ", null));
        groupDao.joinGroup(kate.getId(), group.getId());
        groupDao.subscribeToGroup(anya.getId(), group.getId());

        assertTrue(subscriptionDao.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_onlyMembershipWithoutSubscription_returnsFalse() throws SQLException {
        User anya = createUser("anya");
        User kate = createUser("kate");
        Group group = groupDao.create(new Group(0, "972501 ТГУ", null));
        groupDao.joinGroup(kate.getId(), group.getId());
        groupDao.joinGroup(anya.getId(), group.getId());

        assertFalse(subscriptionDao.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectlyOrViaGroup_noSubscription_returnsFalse() throws SQLException {
        User anya = createUser("anya");
        User kate = createUser("kate");

        assertFalse(subscriptionDao.isSubscribedDirectlyOrViaGroup(anya.getId(), kate.getId()));
    }

    @Test
    void isSubscribedDirectly_ignoresGroupSubscription() throws SQLException {
        User anya = createUser("anya");
        User kate = createUser("kate");
        Group group = groupDao.create(new Group(0, "Клуб", null));
        groupDao.joinGroup(kate.getId(), group.getId());
        groupDao.subscribeToGroup(anya.getId(), group.getId());

        assertFalse(subscriptionDao.isSubscribedDirectly(anya.getId(), kate.getId()));

        subscriptionDao.subscribe(anya.getId(), kate.getId());

        assertTrue(subscriptionDao.isSubscribedDirectly(anya.getId(), kate.getId()));
    }
}
